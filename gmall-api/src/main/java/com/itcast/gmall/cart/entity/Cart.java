package com.itcast.gmall.cart.entity;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 购物车信息
 */

@Setter
public class Cart implements Serializable {

	@Getter
	private List<CartItem> cartItems;//所有的购物项集合
	private Integer count;//商品总数
	private BigDecimal totalPrice;//已选中商品的总价格

	public Integer getCount() {
		AtomicInteger atomicInteger = new AtomicInteger(0);
		cartItems.forEach((cartItem)->{
			atomicInteger.getAndAdd(cartItem.getCount());
		});
		return atomicInteger.get();
	}

	public BigDecimal getTotalPrice() {
		AtomicReference<BigDecimal> allTotalPrice = new AtomicReference<>(new BigDecimal("0"));
		cartItems.forEach((cartItem)->{
			BigDecimal addResult = allTotalPrice.get().add(cartItem.getTotalPrice());
			allTotalPrice.set(addResult);
		});
		return allTotalPrice.get();
	}
}
