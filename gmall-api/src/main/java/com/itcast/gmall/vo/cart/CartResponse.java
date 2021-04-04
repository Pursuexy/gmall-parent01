package com.itcast.gmall.vo.cart;

import com.itcast.gmall.cart.entity.Cart;
import com.itcast.gmall.cart.entity.CartItem;
import lombok.Data;
import java.io.Serializable;

/**
 * 购物车返回信息模板
 */

@Data
public class CartResponse implements Serializable {

	private Cart cart;//整个购物车

	private CartItem cartItem;//购物项信息

	private String cartKey;//离线购物车的数据结构中的key
}
