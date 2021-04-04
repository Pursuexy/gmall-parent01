package com.itcast.gmall.cart.service;

import com.itcast.gmall.cart.entity.CartItem;
import com.itcast.gmall.vo.cart.CartResponse;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 购物车服务
 */
public interface CartService {

	/**
	 * 添加商品到购物车
	 * @param skuId
	 * @param accessToken
	 * @param cartKey
	 * @return
	 */
	CartResponse addToCart(Long skuId,Integer num, String accessToken, String cartKey) throws ExecutionException, InterruptedException;

	/**
	 * 修改购物项数量
	 * @param skuId
	 * @param num
	 * @param accessToken
	 * @param cartKey
	 * @return
	 */
	CartResponse updateCartItemNum(Long skuId, Integer num, String accessToken, String cartKey);

	/**
	 * 查看购物车清单
	 * @param accessToken
	 * @param cartKey
	 * @return
	 */
	CartResponse getCartList(String accessToken, String cartKey);

	/**
	 * 根据skuId删除购物项
	 * @param skuId
	 * @param accessToken
	 * @param cartKey
	 * @return
	 */
	CartResponse deleteCartItem(Long skuId, String accessToken, String cartKey);

	/**
	 * 根据skuId清空购物项
	 * @param accessToken
	 * @param cartKey
	 * @return
	 */
	CartResponse clearCartItem(String accessToken, String cartKey);

	/**
	 * 根据skuIds操作购物项
	 * @param skuIds
	 * @param options
	 * @param accessToken
	 * @param cartKey
	 * @return
	 */
	CartResponse checkCartItems(String skuIds, Integer options, String accessToken, String cartKey);


	/**
	 * 根据用户id查询订单中的购物项列表
	 * @param accessToken
	 * @return
	 */
	List<CartItem> getCartItemsForOrder(String accessToken);

	/**
	 * 清除购物车中已下单的商品
	 * @param accessToken
	 * @param skuIds
	 */
	void removeCartItem(String accessToken, List<Long> skuIds);
}
