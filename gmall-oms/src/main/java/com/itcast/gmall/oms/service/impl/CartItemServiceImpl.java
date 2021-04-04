package com.itcast.gmall.oms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itcast.gmall.oms.entity.CartItem;
import com.itcast.gmall.oms.mapper.CartItemMapper;
import com.itcast.gmall.oms.service.CartItemService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 购物车表 服务实现类
 * </p>
 *
 * @author Pursuexy
 * @since 2021-02-26
 */
@Service
public class CartItemServiceImpl extends ServiceImpl<CartItemMapper, CartItem> implements CartItemService {

}
