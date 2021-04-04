package com.itcast.gmall.pms.service.impl;

import com.itcast.gmall.pms.entity.ProductFullReduction;
import com.itcast.gmall.pms.mapper.ProductFullReductionMapper;
import com.itcast.gmall.pms.service.ProductFullReductionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 产品满减表(只针对同商品) 服务实现类
 * </p>
 *
 * @author Pursuexy
 * @since 2021-02-26
 */
@Service
public class ProductFullReductionServiceImpl extends ServiceImpl<ProductFullReductionMapper, ProductFullReduction> implements ProductFullReductionService {

}
