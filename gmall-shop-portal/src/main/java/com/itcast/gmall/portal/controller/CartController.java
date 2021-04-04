package com.itcast.gmall.portal.controller;

import com.itcast.gmall.cart.service.CartService;
import com.itcast.gmall.utils.CommonResult;
import com.itcast.gmall.vo.cart.CartResponse;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

/**
 * 购物车
 */
@RequestMapping("/cart")
@RestController
public class CartController {

	@Reference
	private CartService cartService;

	/**
	 * 添加商品到购物车
	 * @param skuId
	 * @param accessToken
	 * @param cartKey
	 * @return
	 */
	@ApiOperation("添加购物车")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "skuId", value = "商品的skuId"),
			@ApiImplicitParam(name = "num", value = "商品数量", defaultValue = "1"),
			@ApiImplicitParam(name = "accessToken", value = "登录后的访问令牌，没登录时可不传参数"),
			@ApiImplicitParam(name = "cartKey", value = "离线购物车key，没有时使用随机key进行赋值")
	})
	@PostMapping("/add")
	public CommonResult addToCart(@RequestParam("skuId") Long skuId,
	                              @RequestParam(value = "num", defaultValue = "1") Integer num,
	                              @RequestParam(value = "accessToken", required = false) String accessToken,
	                              @RequestParam(value = "cartKey", required = false) String cartKey) throws ExecutionException, InterruptedException {
		CartResponse cartResponse = cartService.addToCart(skuId, num, accessToken, cartKey);
		return new CommonResult().success(cartResponse);
	}

	/**
	 * 修改购物项数量
	 * @param skuId
	 * @param num
	 * @param accessToken
	 * @param cartKey
	 * @return
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@ApiOperation("更新购物车")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "skuId", value = "商品的skuId"),
			@ApiImplicitParam(name = "num", value = "商品数量", defaultValue = "1"),
			@ApiImplicitParam(name = "accessToken", value = "登录后的访问令牌，没登录时可不传参数"),
			@ApiImplicitParam(name = "cartKey", value = "离线购物车key，没有时使用随机key进行赋值")
	})
	@PostMapping("/update")
	public CommonResult updateCartItemNum(@RequestParam("skuId") Long skuId,
	                                      @RequestParam(value = "num", defaultValue = "1") Integer num,
	                                      @RequestParam(value = "accessToken", required = false) String accessToken,
	                                      @RequestParam(value = "cartKey", required = false) String cartKey) throws ExecutionException, InterruptedException {
		CartResponse cartResponse = cartService.updateCartItemNum(skuId, num, accessToken, cartKey);
		return new CommonResult().success(cartResponse);
	}

	/**
	 * 查看购物车清单
	 *
	 * @param accessToken
	 * @param cartKey
	 * @return
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@ApiOperation("查看购物车")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "accessToken", value = "登录后的访问令牌，没登录时可不传参数"),
			@ApiImplicitParam(name = "cartKey", value = "离线购物车key，没有时使用随机key进行赋值")
	})
	@GetMapping("/cartList")
	public CommonResult getCartList(@RequestParam(value = "accessToken", required = false) String accessToken,
	                                @RequestParam(value = "cartKey", required = false) String cartKey) throws ExecutionException, InterruptedException {
		CartResponse cartResponse = cartService.getCartList(accessToken, cartKey);
		return new CommonResult().success(cartResponse);
	}

	/**
	 * 根据skuId删除购物项
	 * @param skuId
	 * @param accessToken
	 * @param cartKey
	 * @return
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@ApiOperation("删除购物车")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "skuId", value = "商品的skuId"),
			@ApiImplicitParam(name = "accessToken", value = "登录后的访问令牌，没登录时可不传参数"),
			@ApiImplicitParam(name = "cartKey", value = "离线购物车key，没有时使用随机key进行赋值")
	})
	@PostMapping("/delete")
	public CommonResult deleteCartItem(@RequestParam("skuId") Long skuId,
	                                   @RequestParam(value = "accessToken", required = false) String accessToken,
	                                   @RequestParam(value = "cartKey", required = false) String cartKey) throws ExecutionException, InterruptedException {
		CartResponse cartResponse = cartService.deleteCartItem(skuId, accessToken, cartKey);
		return new CommonResult().success(cartResponse);
	}

	/**
	 * 根据skuId清空购物项
	 * @param accessToken
	 * @param cartKey
	 * @return
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@ApiOperation("清空购物车")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "accessToken", value = "登录后的访问令牌，没登录时可不传参数"),
			@ApiImplicitParam(name = "cartKey", value = "离线购物车key，没有时使用随机key进行赋值")
	})
	@PostMapping("/clear")
	public CommonResult clearCartItem(@RequestParam(value = "accessToken", required = false) String accessToken,
	                                  @RequestParam(value = "cartKey", required = false) String cartKey) throws ExecutionException, InterruptedException {
		CartResponse cartResponse = cartService.clearCartItem(accessToken, cartKey);
		return new CommonResult().success(cartResponse);
	}

	/**
	 * 根据skuIds操作购物项
	 * @param skuIds
	 * @param options
	 * @param accessToken
	 * @param cartKey
	 * @return
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@ApiOperation("批量操作购物项")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "skuIds", value = "商品的skuId集合，用逗号分隔"),
			@ApiImplicitParam(name = "options", value = "购物项的选中状态，1表示选中，0表示不选中"),
			@ApiImplicitParam(name = "accessToken", value = "登录后的访问令牌，没登录时可不传参数"),
			@ApiImplicitParam(name = "cartKey", value = "离线购物车key，没有时使用随机key进行赋值")
	})
	@PostMapping("/check")
	public CommonResult checkCartItemStatus(@RequestParam(value = "skuIds") String skuIds,
	                                        @RequestParam(value = "options") Integer options,
	                                        @RequestParam(value = "accessToken", required = false) String accessToken,
	                                        @RequestParam(value = "cartKey", required = false) String cartKey) throws ExecutionException, InterruptedException {
		CartResponse cartResponse = cartService.checkCartItems(skuIds, options, accessToken, cartKey);
		return new CommonResult().success(cartResponse);
	}
}
