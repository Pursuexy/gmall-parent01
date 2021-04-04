package com.itcast.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itcast.gmall.constant.EsConstant;
import com.itcast.gmall.es.product.EsProduct;
import com.itcast.gmall.es.product.EsProductAttributeValue;
import com.itcast.gmall.es.product.EsSkuProductInfo;
import com.itcast.gmall.pms.entity.*;
import com.itcast.gmall.pms.mapper.*;
import com.itcast.gmall.pms.service.ProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itcast.gmall.utils.PageInfoVo;
import com.itcast.gmall.vo.product.PmsProductParam;
import com.itcast.gmall.vo.product.PmsProductQueryParam;
import io.searchbox.client.JestClient;
import io.searchbox.core.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.*;

/**
 * <p>
 * 商品信息 服务实现类
 * </p>
 *
 * @author Pursuexy
 * @since 2021-02-26
 */
@Slf4j
@Component
@Service(cluster = "failfast")
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

	@Autowired
	private ProductMapper productMapper;

	@Autowired
	private ProductAttributeValueMapper productAttributeValueMapper;

	@Autowired
	private ProductFullReductionMapper productFullReductionMapper;

	@Autowired
	private ProductLadderMapper productLadderMapper;

	@Autowired
	private SkuStockMapper skuStockMapper;

	@Autowired
	private JestClient jestClient;

	//当前线程共享数据productId
	private ThreadLocal<Long> threadLocal=new ThreadLocal<>();
	//ThreadLocal原理：
	//private Map<Thread, Long> map = new HashMap<>();
	//存入值：map.put(Thread.currentThread(), product.getId());
	//取出值：Long productId = map.get(Thread.currentThread());
	// 		System.out.println(productId);

	/**
	 * 查询商品详情
	 * @param id
	 * @return
	 */
	@Override
	public Product productInfo(Long id) {
		return productMapper.selectById(id);
	}

	/**
	 * 根据复杂查询条件返回分页数据
	 * @param productQueryParam
	 * @return
	 */
	@Override
	public PageInfoVo productPageInfo(PmsProductQueryParam productQueryParam) {
		QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
		if (productQueryParam.getBrandId() != null) {
			queryWrapper.eq("brand_id", productQueryParam.getBrandId());
		}
		if (!StringUtils.isEmpty(productQueryParam.getKeyword())) {
			queryWrapper.like("name", productQueryParam.getKeyword());
		}
		if (productQueryParam.getProductCategoryId() != null) {
			queryWrapper.eq("product_category_id", productQueryParam.getProductCategoryId());
		}
		if (!StringUtils.isEmpty(productQueryParam.getProductSn())) {
			queryWrapper.like("product_sn", productQueryParam.getProductSn());
		}
		if (productQueryParam.getPublishStatus() != null) {
			queryWrapper.eq("product_status", productQueryParam.getPublishStatus());
		}
		if (productQueryParam.getVerifyStatus() != null) {
			queryWrapper.eq("verify_status", productQueryParam.getVerifyStatus());
		}
		IPage<Product> page = productMapper.selectPage(new Page<Product>(productQueryParam.getPageNum(), productQueryParam.getPageSize()), queryWrapper);
		PageInfoVo pageInfoVo = new PageInfoVo(page.getTotal(),page.getPages(),productQueryParam.getPageSize(),page.getRecords(),page.getCurrent());
		return pageInfoVo;
	}

	/**
	 * 保存商品信息
	 * @param productParam
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void saveProduct(PmsProductParam productParam) {
		//自身service调用自身方法的无法添加真正意义上的事务行为
		ProductServiceImpl currentProxy = (ProductServiceImpl) AopContext.currentProxy();
		//保存商品的基本信息
		currentProxy.saveBaseProductInfo(productParam);
		//保存商品的基本信息
		currentProxy.saveProductAttributeValue(productParam);
		//以下都可以try······catch····
		//保存商品的满减信息
		currentProxy.saveProductFullReduction(productParam);
		//保存商品的阶梯价格
		currentProxy.saveProductLadder(productParam);
		//保存商品的sku表
		currentProxy.saveSkuStock(productParam);
	}

	/**
	 * 批量上下架
	 * @param ids
	 * @param publishStatus
	 */
	@Override
	public void updatePublishStatus(List<Long> ids, Integer publishStatus) {
		//1.更改数据库的商品状态信息
		if (publishStatus == 0) {
			//下架
			ids.forEach((id)->{
				//更改数据库中状态信息
				setProductPublishStatus(publishStatus, id);
				//删除es库中信息
				deleteProductToEs(id);
			});
		} else {
			//上架
			ids.forEach((id)->{
				//更改数据库的商品状态信息
				setProductPublishStatus(publishStatus, id);
				//添加es库中信息
				saveProductToEs(id);
			});
		}
	}

	/**
	 * 查询商品信息详情
	 * @param id
	 * @return
	 */
	@Override
	public EsProduct productAllInfo(Long id) {
		EsProduct esProduct = null;
		//按照id查出商品
		SearchSourceBuilder builder = new SearchSourceBuilder();
		builder.query(QueryBuilders.termQuery("id", id));
		Search search = new Search.Builder(builder.toString())
				.addType(EsConstant.PRODUCT_INFO_ES_TYPE)
				.addIndex(EsConstant.PRODUCT_ES_INDEX)
				.build();
		try {
			SearchResult execute = jestClient.execute(search);
			List<SearchResult.Hit<EsProduct, Void>> hits = execute.getHits(EsProduct.class);
			esProduct = hits.get(0).source;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return esProduct;
	}

	/**
	 * 根据skuId查询商品信息
	 * @param id
	 * @return
	 */
	@Override
	public EsProduct productSkuInfo(Long id) {
		EsProduct esProduct = null;
		//按照id查出商品
		SearchSourceBuilder builder = new SearchSourceBuilder();
		builder.query(QueryBuilders.nestedQuery("skuProductInfos",QueryBuilders.termQuery("skuProductInfos.id",id), ScoreMode.None));
		Search search = new Search.Builder(builder.toString())
				.addType(EsConstant.PRODUCT_INFO_ES_TYPE)
				.addIndex(EsConstant.PRODUCT_ES_INDEX)
				.build();
		try {
			SearchResult execute = jestClient.execute(search);
			List<SearchResult.Hit<EsProduct, Void>> hits = execute.getHits(EsProduct.class);
			esProduct = hits.get(0).source;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return esProduct;
	}

	private void deleteProductToEs(Long id) {
		Delete build = new Delete.Builder(id.toString()).index(EsConstant.PRODUCT_ES_INDEX).type(EsConstant.PRODUCT_INFO_ES_TYPE).build();
		try {
			DocumentResult execute =  jestClient.execute(build);
			boolean succeeded = execute.isSucceeded();
			if (succeeded) {
				log.info("es库中：id为{}的商品信息下架成功",id);
			}else {
				log.error("es库中：id为{}的商品信息下架失败",id);
				//deleteProductToEs(id);//遗留隐患
			}
		} catch (IOException e) {
			log.error("es下架商品信息:id为{}数据异常:{}",id,e.getMessage());
		}
	}

	private void saveProductToEs(Long id) {
		Product productInfo = productInfo(id);

		//2.保存es库的商品信息，也要存入sku信息
		//2.1 复制商品的基本信息
		EsProduct esProduct = new EsProduct();
		BeanUtils.copyProperties(productInfo,esProduct);

		//2.1 复制商品的sku属性信息
		QueryWrapper<SkuStock> queryWrapper = new QueryWrapper<SkuStock>().eq("product_id", id);
		List<SkuStock> skuStockList = skuStockMapper.selectList(queryWrapper);
		//返回的结果信息封装
		List<EsSkuProductInfo> esSkuProductInfos = new ArrayList<>(skuStockList.size());
		//查询当前商品的sku属性信息名称,sku销售属性值是变化的，需要用es的统计信息
		List<ProductAttribute> esProductSkuAttributeNames = productAttributeValueMapper.selectProductSaleAttributeName(id);
		skuStockList.forEach((skuStock)->{
			EsSkuProductInfo esSkuProductInfo = new EsSkuProductInfo();
			BeanUtils.copyProperties(skuStock,esSkuProductInfo);

			//填充商品sku特色标题(商品名称+商品sku销售属性1，2，3)
			String subSkuTitle = esProduct.getName();
			if (!StringUtils.isEmpty(skuStock.getSp1())) {
				subSkuTitle+=" "+skuStock.getSp1();
			}
			if (!StringUtils.isEmpty(skuStock.getSp2())) {
				subSkuTitle+=" "+skuStock.getSp2();
			}
			if (!StringUtils.isEmpty(skuStock.getSp3())) {
				subSkuTitle+=" "+skuStock.getSp3();
			}
			esSkuProductInfo.setSkuTitle(subSkuTitle);

			//封装动态的sku销售属性值到es库中
			List<EsProductAttributeValue> esProductSkuAttributeValues = new ArrayList<>();
			//3.复制商品的spu信息,封装进自定义的EsProductAttributeValue
			for (int i = 0; i < esProductSkuAttributeNames.size(); i++) {
				EsProductAttributeValue esProductAttributeValue = new EsProductAttributeValue();
				esProductAttributeValue.setProductAttributeId(esProductSkuAttributeNames.get(i).getId());
				esProductAttributeValue.setName(esProductSkuAttributeNames.get(i).getName());
				esProductAttributeValue.setProductId(id);
				esProductAttributeValue.setType(esProductSkuAttributeNames.get(i).getType());
				if (i == 0) {
					esProductAttributeValue.setValue(skuStock.getSp1());
				}
				if (i == 1) {
					esProductAttributeValue.setValue(skuStock.getSp2());
				}
				if (i == 2) {
					esProductAttributeValue.setValue(skuStock.getSp3());
				}
				esProductSkuAttributeValues.add(esProductAttributeValue);
			}
			esSkuProductInfo.setEsProductAttributeValues(esProductSkuAttributeValues);
			esSkuProductInfos.add(esSkuProductInfo);
		});
		esProduct.setSkuProductInfos(esSkuProductInfos);

		//3.复制商品的spu信息,封装进自定义的EsProductAttributeValue
		List<EsProductAttributeValue> esProductAttributeValues = productAttributeValueMapper.selectProductBaseAttributeAndValue(id);
		esProduct.setAttrValueList(esProductAttributeValues);

		esProduct.setSkuProductInfos(esSkuProductInfos);

		//把商品保存到es中
		try {
			Index build = new Index.Builder(esProduct).index(EsConstant.PRODUCT_ES_INDEX).type(EsConstant.PRODUCT_INFO_ES_TYPE).id(id.toString()).build();
			DocumentResult execute = jestClient.execute(build);
			boolean succeeded = execute.isSucceeded();
			if (succeeded) {
				log.info("es库中：id为{}商品信息上架成功",id);
			}else{
				log.error("es库中：id为{}商品信息上架失败,商品信息尝试重新上架中",id);
				//saveProductToEs(id);//留有隐患
			}
		} catch (IOException e) {
			log.error("es保存商品信息:id为{}数据异常:{}",id,e.getMessage());
		}

	}

	private void setProductPublishStatus(Integer publishStatus, Long id) {
		//JavaBean使用包装类型
		Product product = new Product();
		//默认属性为空
		product.setId(id);
		product.setPublishStatus(publishStatus);
		//mybatis-plus自带的已经更新不为空的字段
		productMapper.updateById(product);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveSkuStock(PmsProductParam productParam) {
		//保存商品的sku表
		List<SkuStock> skuStockList = productParam.getSkuStockList();
		for (int i = 1; i <= skuStockList.size(); i++) {
			SkuStock skuStock = skuStockList.get(i-1);
			if (StringUtils.isEmpty(skuStock.getSkuCode())) {
				//skuCode的规则  skuId+productId+
				skuStock.setSkuCode(threadLocal.get() +"_"+ i);
			}
			skuStock.setProductId(threadLocal.get());
			skuStockMapper.insert(skuStock);
		}
		log.debug("当前线程·····{}····{}",Thread.currentThread().getId(),Thread.currentThread().getName());
<<<<<<< HEAD
	}
	/**
	 * 查询当前skuId的商品详情信息
	 *
	 * @param skuId
	 * @return
	 */
	@Override
	public SkuStock getSkuInfoBySkuId(Long skuId) {
		return skuStockMapper.selectById(skuId);
=======
>>>>>>> origin/master
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveProductLadder(PmsProductParam productParam) {
		//保存商品的阶梯价格
		List<ProductLadder> productLadderList = productParam.getProductLadderList();
		productLadderList.forEach((productLadder)->{
			productLadder.setProductId(threadLocal.get());
			productLadderMapper.insert(productLadder);
		});
		log.debug("当前线程·····{}····{}",Thread.currentThread().getId(),Thread.currentThread().getName());
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveProductFullReduction(PmsProductParam productParam) {
		//保存商品的满减信息
		List<ProductFullReduction> productFullReductionList = productParam.getProductFullReductionList();
		productFullReductionList.forEach((productFullReduction)->{
			productFullReduction.setProductId(threadLocal.get());
			productFullReductionMapper.insert(productFullReduction);
		});
		log.debug("当前线程·····{}····{}",Thread.currentThread().getId(),Thread.currentThread().getName());
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveProductAttributeValue(PmsProductParam productParam) {
		//保存商品属性列表值
		List<ProductAttributeValue> productAttributeValueList = productParam.getProductAttributeValueList();
		productAttributeValueList.forEach((productAttributeValue)->{
			productAttributeValue.setProductId(threadLocal.get());
			productAttributeValueMapper.insert(productAttributeValue);
		});
		log.debug("当前线程·····{}····{}",Thread.currentThread().getId(),Thread.currentThread().getName());
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@NotNull
	public void saveBaseProductInfo(PmsProductParam productParam) {
		//保存商品的基本信息
		Product product = new Product();
		BeanUtils.copyProperties(productParam,product);
		productMapper.insert(product);
		threadLocal.set(product.getId());
		log.debug("当前线程·····{}····{}",Thread.currentThread().getId(),Thread.currentThread().getName());
	}
}
