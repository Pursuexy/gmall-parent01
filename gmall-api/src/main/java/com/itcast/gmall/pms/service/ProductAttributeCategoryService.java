package com.itcast.gmall.pms.service;

import com.itcast.gmall.pms.entity.ProductAttributeCategory;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itcast.gmall.utils.PageInfoVo;

/**
 * <p>
 * 产品属性分类表 服务类
 * </p>
 *
 * @author Pursuexy
 * @since 2021-02-26
 */
public interface ProductAttributeCategoryService extends IService<ProductAttributeCategory> {

	/**
	 * 分页获取所有商品属性分类
	 * @param pageSize
	 * @param pageNum
	 * @return
	 */
	PageInfoVo productAttributeCategoryServicePageInfo(Integer pageSize, Integer pageNum);
}
