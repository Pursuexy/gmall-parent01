package com.itcast.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itcast.gmall.pms.entity.ProductAttributeCategory;
import com.itcast.gmall.pms.mapper.ProductAttributeCategoryMapper;
import com.itcast.gmall.pms.service.ProductAttributeCategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itcast.gmall.utils.PageInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 产品属性分类表 服务实现类
 * </p>
 *
 * @author Pursuexy
 * @since 2021-02-26
 */
@Component
@Service
public class ProductAttributeCategoryServiceImpl extends ServiceImpl<ProductAttributeCategoryMapper, ProductAttributeCategory> implements ProductAttributeCategoryService {

	@Autowired
	private ProductAttributeCategoryMapper productAttributeCategoryMapper;

	/**
	 * 分页获取所有商品属性分类
	 * @param pageSize
	 * @param pageNum
	 * @return
	 */
	@Override
	public PageInfoVo productAttributeCategoryServicePageInfo(Integer pageSize, Integer pageNum) {
		IPage<ProductAttributeCategory> page = productAttributeCategoryMapper.selectPage(new Page<ProductAttributeCategory>( pageNum,pageSize), null);
		return PageInfoVo.getPageInfoVo(page,pageSize.longValue());
	}
}
