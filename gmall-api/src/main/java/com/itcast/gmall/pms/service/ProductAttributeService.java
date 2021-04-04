package com.itcast.gmall.pms.service;

import com.itcast.gmall.pms.entity.ProductAttribute;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itcast.gmall.utils.PageInfoVo;

/**
 * <p>
 * 商品属性参数表 服务类
 * </p>
 *
 * @author Pursuexy
 * @since 2021-02-26
 */
public interface ProductAttributeService extends IService<ProductAttribute> {

	/**
	 * 根据分类查询销售属性列表或参数列表
	 * @param cid
	 * @param type
	 * @param pageSize
	 * @param pageNum
	 * @return
	 */
	PageInfoVo getCategoryAttributes(Long cid, Integer type, Integer pageSize, Integer pageNum);
}
