package com.itcast.gmall.portal.controller;

import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.fastjson.JSON;
import com.itcast.gmall.constant.SystemCacheConstant;
import com.itcast.gmall.oms.entity.OrderConfirm;
import com.itcast.gmall.oms.entity.OrderCreate;
import com.itcast.gmall.oms.service.OrderService;
import com.itcast.gmall.ums.entity.Member;
import com.itcast.gmall.utils.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Api(tags = "订单服务")
@RestController
@RequestMapping("/order")
public class OrderController {

	@Autowired
	private OrderService orderService;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@ApiOperation( "订单确认")
	@GetMapping("/confirm")
	public CommonResult confirmOrder(@RequestParam("accessToken")String accessToken) {
		//0、检查用户是否存在
		String memberJson = stringRedisTemplate.opsForValue().get(SystemCacheConstant.LOGIN_MEMBER + accessToken);
		if (StringUtils.isEmpty(accessToken) || StringUtils.isEmpty(memberJson)) {
			//用户未登录
			CommonResult commonResult = new CommonResult().failed();
			commonResult.setMessage("用户未登录，请登录");
			return commonResult;
		}
		//1、找到登陆信息
		Member member = JSON.parseObject(memberJson, Member.class);
		//dubbo隐式传参
		RpcContext.getContext().setAttachment("accessToken", accessToken);
		//订单确认
		OrderConfirm orderConfirm = orderService.orderConfirm(member.getId());
		return new CommonResult().success(orderConfirm);
	}

	/**
	 * 创建订单的数据
	 * @param totalPrice
	 * @param accessToken
	 * @return
	 */
	@ApiOperation("下单")
	@PostMapping("/create")
	public CommonResult createOrder(@RequestParam("totalPrice") BigDecimal totalPrice,
	                                @RequestParam("accessToken")String accessToken,
	                                @RequestParam("addressId") Long addressId,
	                                @RequestParam(value = "orderNote",required = false) String orderNote,
	                                @RequestParam("orderToken") String orderToken) {
		//使用AccessToken比较频繁时，可考虑隐式传参
		RpcContext.getContext().setAttachment("accessToken", accessToken);
		//防止重复订单提交
		RpcContext.getContext().setAttachment("orderToken", orderToken);
		//创建订单，要生成订单总额和订单项详情
		OrderCreate orderCreate = orderService.createOrder(totalPrice,addressId,orderNote);
		if (!StringUtils.isEmpty(orderCreate.getToken())) {
			CommonResult failed = new CommonResult().failed();
			failed.setMessage(orderCreate.getToken());
			return failed;
		}
		return new CommonResult().success(orderCreate);
	}
}
