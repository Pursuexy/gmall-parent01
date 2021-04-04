package com.itcast.gmall.pms.mapper;

import com.itcast.gmall.pms.entity.ProductCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itcast.gmall.vo.product.PmsProductCategoryWithChildrenItem;
import java.util.List;

/**
 * <p>
 * 产品分类 Mapper 接口
 * </p>
 *
 * @author Pursuexy
 * @since 2021-02-26
 */
public interface ProductCategoryMapper extends BaseMapper<ProductCategory> {

	List<PmsProductCategoryWithChildrenItem> listCategoryWithChildren(Integer id);
}
