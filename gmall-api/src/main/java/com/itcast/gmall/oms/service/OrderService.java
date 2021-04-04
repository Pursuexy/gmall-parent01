package com.itcast.gmall.oms.service;

import com.itcast.gmall.oms.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itcast.gmall.oms.entity.OrderConfirm;
import com.itcast.gmall.oms.entity.OrderCreate;
import java.math.BigDecimal;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author Pursuexy
 * @since 2021-02-26
 */
public interface OrderService extends IService<Order> {
	/**
	 * 订单确认
	 * @param id
	 * @return
	 */
	OrderConfirm orderConfirm(Long id);

	/**
	 * 创建订单
	 * @param totalPrice
	 * @param addressId
	 * @return
	 */
	OrderCreate createOrder(BigDecimal totalPrice, Long addressId, String orderNote);
}
