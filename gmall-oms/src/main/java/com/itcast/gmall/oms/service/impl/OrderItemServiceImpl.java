package com.itcast.gmall.oms.service.impl;

import com.itcast.gmall.oms.entity.OrderItem;
import com.itcast.gmall.oms.mapper.OrderItemMapper;
import com.itcast.gmall.oms.service.OrderItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单中所包含的商品 服务实现类
 * </p>
 *
 * @author Pursuexy
 * @since 2021-02-26
 */
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {

}
