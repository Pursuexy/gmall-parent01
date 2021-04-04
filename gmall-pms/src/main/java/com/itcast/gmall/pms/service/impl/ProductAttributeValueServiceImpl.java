package com.itcast.gmall.pms.service.impl;

import com.itcast.gmall.pms.entity.ProductAttributeValue;
import com.itcast.gmall.pms.mapper.ProductAttributeValueMapper;
import com.itcast.gmall.pms.service.ProductAttributeValueService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 存储产品参数信息的表 服务实现类
 * </p>
 *
 * @author Pursuexy
 * @since 2021-02-26
 */
@Service
public class ProductAttributeValueServiceImpl extends ServiceImpl<ProductAttributeValueMapper, ProductAttributeValue> implements ProductAttributeValueService {

}
