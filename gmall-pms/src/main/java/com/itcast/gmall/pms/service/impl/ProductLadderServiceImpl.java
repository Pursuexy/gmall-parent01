package com.itcast.gmall.pms.service.impl;

import com.itcast.gmall.pms.entity.ProductLadder;
import com.itcast.gmall.pms.mapper.ProductLadderMapper;
import com.itcast.gmall.pms.service.ProductLadderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 产品阶梯价格表(只针对同商品) 服务实现类
 * </p>
 *
 * @author Pursuexy
 * @since 2021-02-26
 */
@Service
public class ProductLadderServiceImpl extends ServiceImpl<ProductLadderMapper, ProductLadder> implements ProductLadderService {

}
