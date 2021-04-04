package com.itcast.gmall.pms.service;

import com.itcast.gmall.pms.entity.SkuStock;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;

/**
 * <p>
 * sku的库存 服务类
 * </p>
 *
 * @author Pursuexy
 * @since 2021-02-26
 */
public interface SkuStockService extends IService<SkuStock> {
	/**
	 * 根据skuId查询sku的价格Price
	 * @param skuId
	 * @return
	 */
	BigDecimal getSkuPriceBySkuId(Long skuId);
}
