package com.itcast.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itcast.gmall.constant.SystemCacheConstant;
import com.itcast.gmall.pms.entity.ProductCategory;
import com.itcast.gmall.pms.mapper.ProductCategoryMapper;
import com.itcast.gmall.pms.service.ProductCategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itcast.gmall.vo.product.PmsProductCategoryWithChildrenItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 产品分类 服务实现类
 * </p>
 *
 * @author Pursuexy
 * @since 2021-02-26
 */
@Slf4j
@Component
@Service
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements ProductCategoryService {

	@Autowired
	private ProductCategoryMapper productCategoryMapper;

	@Autowired
	private RedisTemplate<Object, Object> redisTemplate;

	/**
	 * 查询三级分类以及子分类
	 * @param id
	 * @return
	 */
	@Override
	public List<PmsProductCategoryWithChildrenItem> listCategoryWithChildren(Integer id) {
		Object cacheMenu = redisTemplate.opsForValue().get(SystemCacheConstant.CATEGORY_MENU_CACHE_KEY);
		List<PmsProductCategoryWithChildrenItem> categoryWithChildrenItemList;
		if (cacheMenu != null) {
			log.debug("菜单数据命中缓存。。。。。");
			categoryWithChildrenItemList = (List<PmsProductCategoryWithChildrenItem>) cacheMenu;
		} else {
			categoryWithChildrenItemList = productCategoryMapper.listCategoryWithChildren(id);
			//将数据转变成缓存数据
			redisTemplate.opsForValue().set(SystemCacheConstant.CATEGORY_MENU_CACHE_KEY, categoryWithChildrenItemList);
		}
		return productCategoryMapper.listCategoryWithChildren(id);
	}
}
