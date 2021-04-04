package com.itcast.gmall.oms.entity;


import com.itcast.gmall.cart.entity.CartItem;
import com.itcast.gmall.sms.entity.Coupon;
import com.itcast.gmall.ums.entity.MemberReceiveAddress;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderConfirm implements Serializable {

	private List<CartItem> cartItems;//购物项列表
	private List<MemberReceiveAddress> memberReceiveAddresses;//地址列表
	private List<Coupon> coupons;//优惠券
	//支付方式、配送方式

	//防止重复提交订单
	private String orderToken;//防止重复订单令牌
	private BigDecimal totalPrice=new BigDecimal("0");//总价格
	private BigDecimal priceTotalPrice=new BigDecimal("0");//商品总价格
	private Integer count=0;//商品总数量
	private BigDecimal couponPrice=new BigDecimal("0");//优惠券减免总价格
	private BigDecimal transPrice=new BigDecimal("10");//运费总价格

}
