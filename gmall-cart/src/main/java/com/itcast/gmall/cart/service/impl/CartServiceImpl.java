package com.itcast.gmall.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.itcast.gmall.cart.component.MemberComponent;
import com.itcast.gmall.cart.entity.Cart;
import com.itcast.gmall.cart.entity.CartConstant;
import com.itcast.gmall.cart.entity.CartItem;
import com.itcast.gmall.cart.entity.UserCartKey;
import com.itcast.gmall.cart.service.CartService;
import com.itcast.gmall.pms.entity.Product;
import com.itcast.gmall.pms.service.ProductService;
import com.itcast.gmall.pms.service.SkuStockService;
import com.itcast.gmall.ums.entity.Member;
import com.itcast.gmall.vo.cart.CartResponse;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Component
public class CartServiceImpl implements CartService {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	private MemberComponent memberComponent;

	@Autowired
	private RedissonClient RedissonClient;

	@Reference
	private SkuStockService skuStockService;

	@Reference
	private ProductService productService;

	/**
	 * 添加商品到购物车
	 * @param skuId
	 * @param accessToken
	 * @param cartKey
	 * @return
	 */
	@Override
	public CartResponse addToCart(Long skuId,Integer num, String accessToken, String cartKey) throws ExecutionException, InterruptedException {

		Member member = memberComponent.getMemberByAccessToken(accessToken);
		if (member != null && !StringUtils.isEmpty(cartKey)) {
			//合并离线购物车商品信息
			mergeCart(cartKey, member.getId(),accessToken);
		}

		//String finalCartKey = "";
		// if (member != null) {
		//1.用户登录，购物车用在线购物车，cart：user：skuId
		// 	finalCartKey = CartConstant.USER_CART_KEY_PREFIX + member.getId();

		//添加商品到购物车
		// 	CartItem cartItem = addCartItemToCart(skuId,num, finalCartKey);
		// 	CartResponse cartResponse = new CartResponse();
		// 	cartResponse.setCartItem(cartItem);
		// 	return cartResponse;
		// }

		// if (StringUtils.isEmpty(cartKey)) {
		//2.没登录，使用浏览器临时购物车，cart：temp：cartKey
		// 	finalCartKey = CartConstant.TEMP_CART_KEY_PREFIX + cartKey;

		//添加商品到购物车
		// 	CartItem cartItem = addCartItemToCart(skuId,num,  finalCartKey);
		// 	CartResponse cartResponse = new CartResponse();
		// 	cartResponse.setCartItem(cartItem);
		// 	return cartResponse;
		// }

		//3.参数没完整提供，自动分配临时购物车
		// String newCartKey = UUID.randomUUID().toString().replace("-","");
		// finalCartKey = CartConstant.TEMP_CART_KEY_PREFIX + newCartKey;

	    //添加商品到购物车
		// CartItem cartItem = addCartItemToCart(skuId, num, finalCartKey);
		// CartResponse cartResponse = new CartResponse();
		// cartResponse.setCartItem(cartItem);
		// cartResponse.setCartKey(newCartKey);
		// return cartResponse;

		//针对上面的优化代码
		UserCartKey userCartKey = memberComponent.getCartKey(cartKey, accessToken);
		String finalCartKey = userCartKey.getFinalCartKey();
		//添加商品到购物车
		CartItem cartItem = addCartItemToCart(skuId,num,accessToken, finalCartKey);
		CartResponse cartResponse = new CartResponse();
		cartResponse.setCartItem(cartItem);
		cartResponse.setCartKey(userCartKey.getFinalCartKey());
		cartResponse.setCart(getCartList(accessToken, cartKey).getCart());
		return cartResponse;
	}

	/**
	 * 修改购物项数量
	 * @param skuId
	 * @param num
	 * @param accessToken
	 * @param cartKey
	 * @return
	 */
	@Override
	public CartResponse updateCartItemNum(Long skuId, Integer num, String accessToken, String cartKey) {
		//判断是哪个cartKey
		UserCartKey userCartKey = memberComponent.getCartKey(cartKey, accessToken);
		String finalCartKey = userCartKey.getFinalCartKey();
		RMap<String, String> map = RedissonClient.getMap(finalCartKey);
		String json = map.get(skuId.toString());
		CartItem cartItem = JSON.parseObject(json, CartItem.class);
		cartItem.setCount(num);
		String jsonString = JSON.toJSONString(json);
		map.put(skuId.toString(), jsonString);
		CartResponse cartResponse = new CartResponse();
		cartResponse.setCartItem(cartItem);
		return cartResponse;
	}

	/**
	 * 查看购物车清单
	 * @param accessToken
	 * @param cartKey
	 * @return
	 */
	@Override
	public CartResponse getCartList(String accessToken, String cartKey) {
		UserCartKey userCartKey = memberComponent.getCartKey(cartKey, accessToken);
		//查询用户的购物车时候是否需要判断购物车是否需要进行合并
		if (userCartKey.isLoginStatus()) {
			//用户登录了，必须进行合并购物车
			mergeCart(cartKey,userCartKey.getUserId(),accessToken);
		}
		//查询购物车数据
		String finalCartKey = userCartKey.getFinalCartKey();
		//自动设定时间
		stringRedisTemplate.expire(finalCartKey, 30L, TimeUnit.DAYS);
		RMap<String, String> map = RedissonClient.getMap(finalCartKey);
		Cart cart = new Cart();
		List<CartItem> cartItems = new ArrayList<>();
		CartResponse cartResponse = new CartResponse();
		if (map != null && !map.isEmpty()) {
			//购物项的json数据
			map.forEach((key, value) -> {
				if (!key.equalsIgnoreCase(CartConstant.CART_KEY_CHECKED)) {
					CartItem cartItem = JSON.parseObject(value, CartItem.class);
					cartItems.add(cartItem);
				}
			});
			cart.setCartItems(cartItems);
		}else {
			//用户没有购物车，创建一个空的
			cartResponse.setCartKey(userCartKey.getFinalCartKey());
		}
		cartResponse.setCart(cart);
		return cartResponse;
	}

	/**
	 * 根据skuId删除购物项
	 * @param skuId
	 * @param accessToken
	 * @param cartKey
	 * @return
	 */
	@Override
	public CartResponse deleteCartItem(Long skuId, String accessToken, String cartKey) {
		UserCartKey userCartKey = memberComponent.getCartKey(cartKey, accessToken);
		String finalCartKey = userCartKey.getFinalCartKey();
		//维护购物车状态
		checkCartItems(Arrays.asList(userCartKey.getUserId()).toString(), 0, accessToken, cartKey);
		//删除购物车项
		RMap<String, String> map = RedissonClient.getMap(finalCartKey);
		map.remove(skuId.toString());
		//重新查询购物车清单，返回购物车列表
		CartResponse cartResponse = getCartList(accessToken, cartKey);
		return cartResponse;
	}

	/**
	 * 根据skuId清空购物项
	 * @param accessToken
	 * @param cartKey
	 * @return
	 */
	@Override
	public CartResponse clearCartItem(String accessToken, String cartKey) {
		UserCartKey userCartKey = memberComponent.getCartKey(accessToken, cartKey);
		String finalCartKey = userCartKey.getFinalCartKey();
		RMap<String, String> map = RedissonClient.getMap(finalCartKey);
		map.clear();
		CartResponse cartResponse = new CartResponse();
		cartResponse.setCartKey(userCartKey.getFinalCartKey());//保留购物车的key，可保留也可不保留
		return cartResponse;
	}

	/**
	 * 根据skuIds操作购物项
	 * @param skuIds
	 * @param options
	 * @param accessToken
	 * @param cartKey
	 * @return
	 */
	@Override
	public CartResponse checkCartItems(String skuIds, Integer options, String accessToken, String cartKey) {
		//根据skuIDS查询到cartItem对象的json，更改其中checkStatus状态为false
		UserCartKey userCartKey = memberComponent.getCartKey(cartKey, accessToken);
		String finalCartKey = userCartKey.getFinalCartKey();
		RMap<String, String> cartMap = RedissonClient.getMap(finalCartKey);
		boolean checked = options == 1;
		List<Long> skuIdsList = new ArrayList<Long>();
		//修改购物车状态
		if (!StringUtils.isEmpty(skuIds)) {
			String[] ids = skuIds.split(",");
			for (String id : ids) {
				long skuId = Long.parseLong(id);
				skuIdsList.add(skuId);
				if (cartMap != null && !cartMap.isEmpty()) {
					String cartItemJson = cartMap.get(id);
					CartItem cartItem = JSON.parseObject(cartItemJson, CartItem.class);
					cartItem.setCheckStatus(checked);
					//覆盖Redis原数据
					String jsonString = JSON.toJSONString(cartItem);
					cartMap.put(id, jsonString);
				}
			}
		}
		//修改购物车状态
		assert cartMap != null;
		changeCartItemStatus(cartMap, checked, skuIdsList);
		return getCartList(accessToken, cartKey);
	}

	/**
	 * 修改购物车状态
	 * @param cartMap
	 * @param checked
	 * @param skuIdsList
	 */
	private void changeCartItemStatus(RMap<String, String> cartMap, boolean checked, java.util.Collection<Long> skuIdsList) {
		String checkedJson = cartMap.get(CartConstant.CART_KEY_CHECKED);
		Set<Long> checkedSet = JSON.parseObject(checkedJson, new TypeReference<Set<Long>>() {
		});
		//防止空指针异常
		if (checkedSet == null && checkedSet.isEmpty()) {
			checkedSet = new LinkedHashSet<>();
		}
		//2、方便维护数组，map应该选用key：checked, value：set集合（保证不重复）
		if (checked) {
			//选中才放入checkedSet
			checkedSet.addAll(skuIdsList);
			log.debug("选中状态的商品放入set集合中：{}", checkedSet);
		}else {
			checkedSet.removeAll(skuIdsList);
			log.debug("被移除未选中的商品：{}", checkedSet);
		}
		//重新保存checkedStatus到Redis中map
		cartMap.put(CartConstant.CART_KEY_CHECKED, JSON.toJSONString(checkedSet));
	}


	/**
	 * 合并离线购物车商品信息
	 * @param cartKey
	 * @param id
	 */
	private void mergeCart(String cartKey, Long id,String accessToken) {
		String oldCartKey = CartConstant.TEMP_CART_KEY_PREFIX + cartKey;
		String newCartKey = CartConstant.USER_CART_KEY_PREFIX + id.toString();

		//获取离线购物车map
		RMap<String, String> oldCartMap = RedissonClient.getMap(oldCartKey);
		if (oldCartMap != null && !oldCartMap.isEmpty()) {
			//map不为空且map有数据
			//skuId
			//购物项的json数据
			oldCartMap.forEach((key, value) -> {
				if (!key.equalsIgnoreCase(CartConstant.CART_KEY_CHECKED)) {
					CartItem cartItem = JSON.parseObject(value, CartItem.class);
					try {
						addCartItemToCart(Long.parseLong(key), cartItem.getCount(),accessToken, newCartKey);
						//移除老购物车的key,防止太频繁，最后一次性清空
						//oldCartMap.remove(oldCartKey);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			//移除老购物车的key,防止太频繁，最后一次性清空
			oldCartMap.clear();
		}
	}

	/**
	 * 添加商品到购物车
	 * @param skuId
	 * @param num
	 * @param finalCartKey
	 * @return
	 */
	private CartItem addCartItemToCart(Long skuId,Integer num,String accessToken,  String finalCartKey) throws ExecutionException, InterruptedException {
		//根据skuId查询商品sku信息
		CartItem newCartItem = new CartItem();
		CompletableFuture<Void> skuFuture = CompletableFuture.supplyAsync(() -> {
			return skuStockService.getById(skuId);//查询时间长且慢。考虑未来任务
		}).thenAcceptAsync((skuStock) -> {
			Long productId = skuStock.getProductId();
			Product product = productService.getById(productId);
			BeanUtils.copyProperties(skuStock, newCartItem);
			newCartItem.setName(product.getName());
			newCartItem.setSkuId(productId);
			newCartItem.setCheckStatus(true);
			newCartItem.setCount(num);
		});

		/*
		 * 购物车集合 k[skuId]:string,v[购物项]：string，购物项集合（json)
		 * 其中含有k[checked]:v[选中购物项的数组]
		 */
		RMap<String, String> map = RedissonClient.getMap(finalCartKey);
		//检查是否为空，防止数据叠加
		String itemJson = map.get(skuId.toString());
		//在线阻塞等待结果，拿到skuFuture的异步结果
		skuFuture.get();
		if (!StringUtils.isEmpty(itemJson)) {
			//叠加购物项信息；购物车获取老的item的数量，商品信息用新查询到的数据信息
			CartItem cartItem = JSON.parseObject(itemJson, CartItem.class);
			Integer cartItemCount = cartItem.getCount();
			newCartItem.setCount(cartItemCount+newCartItem.getCount());
			//重新存回map
			String newCartItemJson = JSON.toJSONString(newCartItem);
			map.put(skuId.toString(), newCartItemJson);
		}else {
			//新增购物项
			String newCartItemJson = JSON.toJSONString(newCartItem);
			map.put(skuId.toString(), newCartItemJson);
		}
		//维护购物项的勾选状态
		checkCartItems(Collections.singletonList(skuId).toString(), 1, accessToken, finalCartKey);
		return newCartItem;
	}

	/**
	 * 根据用户accessToken查询订单中的购物项列表
	 * @param accessToken
	 * @return
	 */
	@Override
	public List<CartItem> getCartItemsForOrder(String accessToken) {
		List<CartItem> cartItems = new ArrayList<>();
		//根据用户的accessToken获取CartItemList列表
		UserCartKey cartKey = memberComponent.getCartKey(null,accessToken);
		RMap<String, String> map = RedissonClient.getMap(cartKey.getFinalCartKey());
		String checkedCartItemJson = map.get(CartConstant.CART_KEY_CHECKED);
		Set<Long> cartItemSet = JSON.parseObject(checkedCartItemJson, new TypeReference<Set<Long>>() {
		});
		cartItemSet.forEach((item)->{
			String itemJson = map.get(item.toString());
			CartItem cartItem = JSON.parseObject(itemJson, CartItem.class);
			cartItems.add(cartItem);
		});

		return cartItems;
	}

	/**
	 * 清除购物车中已下单的商品
	 * @param accessToken
	 * @param skuIds
	 */
	@Override
	public void removeCartItem(String accessToken, List<Long> skuIds) {
		UserCartKey cartKey = memberComponent.getCartKey(null,accessToken);
		String finalCartKey = cartKey.getFinalCartKey();
		RMap<String, String> map = RedissonClient.getMap(finalCartKey);
		skuIds.forEach((skuId) -> {
			//移除商品项
			map.remove(skuId.toString());
		});
		//移除状态勾选状态保存
		map.put(CartConstant.CART_KEY_CHECKED, JSON.toJSONString(new LinkedHashSet<Long>()));
	}
}
