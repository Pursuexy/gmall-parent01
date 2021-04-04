package com.itcast.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itcast.gmall.pms.entity.ProductAttribute;
import com.itcast.gmall.pms.mapper.ProductAttributeMapper;
import com.itcast.gmall.pms.service.ProductAttributeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itcast.gmall.utils.PageInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 商品属性参数表 服务实现类
 * </p>
 *
 * @author Pursuexy
 * @since 2021-02-26
 */
@Component
@Service
public class ProductAttributeServiceImpl extends ServiceImpl<ProductAttributeMapper, ProductAttribute> implements ProductAttributeService {

	@Autowired
	private ProductAttributeMapper productAttributeMapper;

	/**
	 * 根据分类查询销售属性列表或参数列表
	 * @param cid
	 * @param type
	 * @param pageSize
	 * @param pageNum
	 * @return
	 */
	@Override
	public PageInfoVo getCategoryAttributes(Long cid, Integer type, Integer pageSize, Integer pageNum) {
		QueryWrapper<ProductAttribute> queryWrapper = new QueryWrapper<ProductAttribute>().eq("product_attribute_category_id", cid).eq("type", type);
		IPage<ProductAttribute> productAttributeIPage = productAttributeMapper.selectPage(new Page<ProductAttribute>(pageNum, pageSize), queryWrapper);
		return PageInfoVo.getPageInfoVo(productAttributeIPage,pageSize.longValue());
	}
}
