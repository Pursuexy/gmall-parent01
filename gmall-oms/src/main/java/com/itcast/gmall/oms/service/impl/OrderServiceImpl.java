package com.itcast.gmall.oms.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.itcast.gmall.cart.entity.CartItem;
import com.itcast.gmall.cart.service.CartService;
import com.itcast.gmall.constant.OrderStatusEnum;
import com.itcast.gmall.constant.SystemCacheConstant;
import com.itcast.gmall.es.product.EsProduct;
import com.itcast.gmall.es.product.EsProductAttributeValue;
import com.itcast.gmall.es.product.EsSkuProductInfo;
import com.itcast.gmall.oms.component.MemberComponent;
import com.itcast.gmall.oms.entity.Order;
import com.itcast.gmall.oms.entity.OrderConfirm;
import com.itcast.gmall.oms.entity.OrderCreate;
import com.itcast.gmall.oms.entity.OrderItem;
import com.itcast.gmall.oms.mapper.OrderItemMapper;
import com.itcast.gmall.oms.mapper.OrderMapper;
import com.itcast.gmall.oms.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itcast.gmall.pms.entity.SkuStock;
import com.itcast.gmall.pms.service.ProductService;
import com.itcast.gmall.pms.service.SkuStockService;
import com.itcast.gmall.ums.entity.Member;
import com.itcast.gmall.ums.entity.MemberReceiveAddress;
import com.itcast.gmall.ums.service.MemberService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author Pursuexy
 * @since 2021-02-26
 */
@Component
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	private MemberComponent memberComponent;

	@Autowired
	private OrderMapper orderMapper;

	@Autowired
	private OrderItemMapper orderItemMapper;

	@Reference
	private MemberService memberService;

	@Reference
	private CartService cartService;

	@Reference
	private ProductService productService;

	@Reference
	private SkuStockService skuStockService;


	private ThreadLocal<List<CartItem>> listThreadLocal = new ThreadLocal<>();

	/**
	 * 订单确认
	 *
	 * @param id
	 * @return
	 */
	@Override
	public OrderConfirm orderConfirm(Long id) {
		//获取隐式传参的参数，不用修改接口便可实现所需要的参数
		String accessToken = RpcContext.getContext().getAttachment("accessToken");
		List<CartItem> cartItems = cartService.getCartItemsForOrder(accessToken);
		OrderConfirm orderConfirm = new OrderConfirm();
		orderConfirm.setMemberReceiveAddresses(memberService.getMemberAddress(id));//会员收货地址
		orderConfirm.setCoupons(null);//会员优惠券信息
		orderConfirm.setCartItems(cartItems);
		orderConfirm.setTransPrice(new BigDecimal("10"));

		String token = UUID.randomUUID().toString().replace("-", "");
		//给令牌设置过期时间，使用业务逻辑去尝试完成其他的验证
		String orderToken = token + "_" + System.currentTimeMillis() + "_" + 10 * 60 * 1000;
		orderConfirm.setOrderToken(orderToken);//设置反重复令牌
		orderConfirm.setCouponPrice(null);
		cartItems.forEach((cartItem) -> {
			Integer count = cartItem.getCount();
			orderConfirm.setCount(orderConfirm.getCount() + count);
			BigDecimal price = cartItem.getTotalPrice();
			orderConfirm.setPriceTotalPrice(orderConfirm.getPriceTotalPrice().add(price));
		});
		orderConfirm.setTotalPrice(null);
		orderConfirm.setTotalPrice(orderConfirm.getPriceTotalPrice().add(orderConfirm.getTransPrice()).multiply(orderConfirm.getCouponPrice()));
		//存入缓存中的防重复令牌
		stringRedisTemplate.opsForSet().add(SystemCacheConstant.ORDER_UNIQUE_TOKEN, orderToken);
		return orderConfirm;
	}

	/**
	 * 创建订单
	 *
	 * @param totalPrice
	 * @param addressId
	 * @return
	 */
	@Transactional
	@Override
	public OrderCreate createOrder(BigDecimal totalPrice, Long addressId, String orderNote) {
		//1、防重复提交订单
		String orderToken = RpcContext.getContext().getAttachment("orderToken");
		//1.1、验证令牌的第一种错误
		if (StringUtils.isEmpty(orderToken)) {
			OrderCreate orderCreate = new OrderCreate();
			orderCreate.setToken("此次操作出现错误，请重新尝试");
			return orderCreate;
		}
		//1.2、验证令牌数据正确性 token=token+"_"+System.CurrentTimeMillis()+"_"+60*10;
		String[] split = orderToken.split("_");
		if (!orderToken.contains("_") && split.length != 3) {
			OrderCreate orderCreate = new OrderCreate();
			orderCreate.setToken("非法操作，请重试");
			return orderCreate;
		}
		long createTime = Long.parseLong(split[1]);
		long timeOut = Long.parseLong(split[2]);
		if (System.currentTimeMillis() - createTime >= timeOut) {
			OrderCreate orderCreate = new OrderCreate();
			orderCreate.setToken("页面超时，请重新刷新");
			return orderCreate;
		}
		//1.3、验证重复
		Long remove = stringRedisTemplate.opsForSet().remove(SystemCacheConstant.ORDER_UNIQUE_TOKEN, orderToken);
		if (remove != 1) {
			OrderCreate orderCreate = new OrderCreate();
			orderCreate.setToken("创建订单失败，请重新创建");
			return orderCreate;
		}
		String accessToken = RpcContext.getContext().getAttachment("accessToken");
		Member member = memberComponent.getMemberByAccessToken(accessToken);
		//1.4、验证价格正确性
		Boolean flag = vaildPrice(totalPrice, accessToken);
		if (!flag) {
			OrderCreate create = new OrderCreate();
			create.setLimit(true);
			create.setToken("订单金额发生变化，请重新提交");
			return create;
		}

		//1.5、保存订单创建信息
		OrderCreate orderCreate = initOrderCreate(totalPrice, addressId, accessToken, member);
		//2、加工处理数据
		//2.1、保存订单信息
		Order order = initOrder(totalPrice, addressId, orderNote, member, orderCreate);
		//保证订单幂等性：1、可利用令牌防重入字段校验  2、数据库幂等性：利用数据防重入的唯一字段校验
		orderMapper.insert(order);
		//2.2、保存订单项信息
		saveOrderItem(accessToken, order);
		//3、清除购物车中已下单的商品
		return null;
	}

	/**
	 * 保存订单项信息
	 *
	 * @param order
	 */
	private void saveOrderItem(String accessToken, Order order) {
		//构造-保存订单项信息
		List<Long> skuIds = new ArrayList<>();
		List<CartItem> cartItems = listThreadLocal.get();
		List<OrderItem> orderItems = new ArrayList<>();
		cartItems.forEach((cartItem) -> {
			skuIds.add(cartItem.getSkuId());
			OrderItem orderItem = new OrderItem();
			orderItem.setOrderId(order.getId());
			orderItem.setOrderSn(order.getOrderSn());
			//查询当前skuId的商品服务信息
			EsProduct esProduct = productService.productSkuInfo(cartItem.getSkuId());
			orderItem.setProductId(esProduct.getId());
			orderItem.setProductName(esProduct.getName());
			orderItem.setProductPic(esProduct.getPic());
			orderItem.setProductAttr(esProduct.getAttrValueList().toString());
			orderItem.setProductBrand(esProduct.getBrandName());
			orderItem.setProductSn(esProduct.getProductSn());
			orderItem.setProductCategoryId(esProduct.getProductCategoryId());
			orderItem.setProductPrice(cartItem.getPrice());//当前购物车的数据库新价格
			orderItem.setProductQuantity(cartItem.getCount());
			orderItem.setProductSkuId(cartItem.getSkuId());

			//销售属性信息
			List<EsSkuProductInfo> skuProductInfos = esProduct.getSkuProductInfos();
			String attributeValueJsonStr = "";
			SkuStock skuStock = new SkuStock();
			for (EsSkuProductInfo skuProductInfo : skuProductInfos) {
				if (cartItem.getSkuId().equals(skuProductInfo.getId())) {
					List<EsProductAttributeValue> attrValueList = skuProductInfo.getEsProductAttributeValues();
					attributeValueJsonStr = JSON.toJSONString(attrValueList);
					BeanUtils.copyProperties(skuProductInfo, skuStock);
				}
			}
			orderItem.setProductAttr(attributeValueJsonStr);
			//查询当前skuId的商品详情信息
			orderItem.setProductSkuCode(skuStock.getSkuCode());
			orderItem.setSp1(skuStock.getSp1());
			orderItem.setSp2(skuStock.getSp2());
			orderItem.setSp3(skuStock.getSp3());
			//保存订单入订单数据库
			orderItemMapper.insert(orderItem);
			orderItems.add(orderItem);
		});
		//清除购物车中已下单的商品
		cartService.removeCartItem(accessToken, skuIds);
	}

	/**
	 * 初始化订单创建---保存订单创建信息
	 *
	 * @param totalPrice
	 * @param addressId
	 * @param accessToken
	 * @param member
	 * @return
	 */
	@NotNull
	private OrderCreate initOrderCreate(BigDecimal totalPrice, Long addressId, String accessToken, Member member) {
		OrderCreate orderCreate = new OrderCreate();
		//设置订单号
		String timeId = IdWorker.getTimeId();
		orderCreate.setOrderSn(timeId);
		//设置收货人地址Id
		orderCreate.setAddressId(addressId);
		//设置商品项列表
		List<CartItem> cartItemsForOrder = cartService.getCartItemsForOrder(accessToken);
		orderCreate.setCartItems(cartItemsForOrder);
		//设置会员Id
		orderCreate.setMemberId(member.getId());
		//设置商品详情
		orderCreate.setDetailInfo(cartItemsForOrder.get(0).getName());
		//设置总价格
		orderCreate.setTotalPrice(totalPrice);
		return orderCreate;
	}

	/**
	 * 初始化订单信息---保存订单信息
	 *
	 * @param totalPrice
	 * @param addressId
	 * @param orderNote
	 * @param member
	 * @param orderCreate
	 */
	private Order initOrder(BigDecimal totalPrice, Long addressId, String orderNote, Member member, OrderCreate orderCreate) {
		//保存订单信息
		Order order = new Order();
		order.setMemberId(member.getId());
		order.setMemberUsername(member.getUsername());
		order.setOrderSn(orderCreate.getOrderSn());
		order.setCreateTime(new Date());
		order.setAutoConfirmDay(7);
		order.setNote(orderNote);
		order.setTotalAmount(totalPrice);
		order.setFreightAmount(new BigDecimal("10.00"));
		order.setStatus(OrderStatusEnum.UNPAY.getCode());

		//根据memberId获取收货人的地址
		MemberReceiveAddress memberReceiveAddress = memberService.getMemberAddressByAddressId(addressId);
		order.setReceiverDetailAddress(memberReceiveAddress.getDetailAddress());
		order.setReceiverName(memberReceiveAddress.getName());
		order.setReceiverCity(memberReceiveAddress.getCity());
		order.setReceiverPhone(memberReceiveAddress.getPhoneNumber());
		order.setReceiverProvince(memberReceiveAddress.getProvince());
		order.setReceiverPostCode(memberReceiveAddress.getPostCode());
		order.setReceiverRegion(memberReceiveAddress.getRegion());
		return order;
	}

	/**
	 * 验证价格正确性
	 *
	 * @param frontPrice
	 * @param accessToken
	 * @return
	 */
	private Boolean vaildPrice(BigDecimal frontPrice, String accessToken) {
		List<CartItem> cartItems = cartService.getCartItemsForOrder(accessToken);
		listThreadLocal.set(cartItems);
		BigDecimal totalPrice = new BigDecimal("0");
		for (CartItem cartItem : cartItems) {
			// BigDecimal price1 = cartItem.getPrice();
			// BigDecimal price = cartItem.getTotalPrice();
			// totalPrice = totalPrice.add(price);
			//根据skuId查询sku的最新价格Price
			BigDecimal newPrice = skuStockService.getSkuPriceBySkuId(cartItem.getSkuId());
			cartItem.setPrice(newPrice);
			Integer count = cartItem.getCount();
			BigDecimal cartItemTotalPrice = newPrice.multiply(new BigDecimal(count.toString()));
			totalPrice = totalPrice.add(cartItemTotalPrice);
		}

		//运费价格
		BigDecimal transPrice = new BigDecimal("10.00");
		totalPrice = totalPrice.add(transPrice);
		return totalPrice.compareTo(frontPrice) == 0;
	}
}
