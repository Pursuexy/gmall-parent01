package com.itcast.gmall.pms.service;

import com.itcast.gmall.pms.entity.ProductCategory;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itcast.gmall.vo.product.PmsProductCategoryWithChildrenItem;

import java.util.List;

/**
 * <p>
 * 产品分类 服务类
 * </p>
 *
 * @author Pursuexy
 * @since 2021-02-26
 */
public interface ProductCategoryService extends IService<ProductCategory> {

	/**
	 * 查询三级分类的菜单
	 * @param id
	 * @return
	 */
	List<PmsProductCategoryWithChildrenItem> listCategoryWithChildren(Integer id);
}
