package com.itcast.gmall.pms.mapper;

import com.itcast.gmall.es.product.EsProductAttributeValue;
import com.itcast.gmall.pms.entity.ProductAttribute;
import com.itcast.gmall.pms.entity.ProductAttributeValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 存储产品参数信息的表 Mapper 接口
 * </p>
 *
 * @author Pursuexy
 * @since 2021-02-26
 */
public interface ProductAttributeValueMapper extends BaseMapper<ProductAttributeValue> {

	/**
	 * 查询商品的基本属性和基本属性值
	 * @param id
	 * @return
	 */
	List<EsProductAttributeValue> selectProductBaseAttributeAndValue(Long id);

	/**
	 * 查询商品的销售属性名称及其动态值
	 * @param id
	 * @return
	 */
	List<ProductAttribute> selectProductSaleAttributeName(Long id);
}
