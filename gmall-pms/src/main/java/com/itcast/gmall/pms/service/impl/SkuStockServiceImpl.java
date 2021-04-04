package com.itcast.gmall.pms.service.impl;

import com.itcast.gmall.pms.entity.SkuStock;
import com.itcast.gmall.pms.mapper.SkuStockMapper;
import com.itcast.gmall.pms.service.SkuStockService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * <p>
 * sku的库存 服务实现类
 * </p>
 *
 * @author Pursuexy
 * @since 2021-02-26
 */
@Service
public class SkuStockServiceImpl extends ServiceImpl<SkuStockMapper, SkuStock> implements SkuStockService {

	@Autowired
	private SkuStockMapper skuStockMapper;

	/**
	 * 根据skuId查询sku的价格Price
	 * @param skuId
	 * @return
	 */
	@Override
	public BigDecimal getSkuPriceBySkuId(Long skuId) {
		return skuStockMapper.selectById(skuId).getPrice();
	}
}
