package com.itcast.gmall.pms.service;

import com.itcast.gmall.es.product.EsProduct;
import com.itcast.gmall.pms.entity.Product;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itcast.gmall.utils.PageInfoVo;
import com.itcast.gmall.vo.product.PmsProductParam;
import com.itcast.gmall.vo.product.PmsProductQueryParam;

import java.util.List;

/**
 * <p>
 * 商品信息 服务类
 * </p>
 *
 * @author Pursuexy
 * @since 2021-02-26
 */
public interface ProductService extends IService<Product> {

	/**
	 * 查询商品详情
	 * @param id
	 * @return
	 */
	Product productInfo(Long id);

	/**
	 * 根据复杂查询条件返回分页数据
	 * @param productQueryParam
	 * @return
	 */
	PageInfoVo productPageInfo(PmsProductQueryParam productQueryParam);

	/**
	 * 保存商品
	 * @param productParam
	 */
	void saveProduct(PmsProductParam productParam);

	/**
	 * 批量上下架
	 * @param ids
	 * @param publishStatus
	 */
	void updatePublishStatus(List<Long> ids, Integer publishStatus);

	/**
	 * 查询商品信息详情
	 * @param id
	 * @return
	 */
	EsProduct productAllInfo(Long id);

	/**
	 * 根据skuId查询商品信息
	 * @param id
	 * @return
	 */
	EsProduct productSkuInfo(Long id);
}
