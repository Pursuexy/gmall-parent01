package com.itcast.gmall.oms.entity;

import com.itcast.gmall.cart.entity.CartItem;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/***
 * 订单创建返回的信息
 */
@Data
public class OrderCreate implements Serializable {
	private String orderSn;//订单编号

	private BigDecimal totalPrice;//订单总价格
	private Long addressId;//订单收货人地址Id
	private Long memberId;//订单收货人信息Id
	private List<CartItem> cartItems;//订单项列表
	private String detailInfo;//订单详情信息
	private Boolean limit;//验证价格正确性的字段t
	private String token;//令牌 token=token+"_"+System.CurrentTimeMillis()+"_"+60*10;

}
