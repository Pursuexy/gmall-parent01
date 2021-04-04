package com.itcast.gmall.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itcast.gmall.es.product.EsProduct;
import com.itcast.gmall.pms.service.ProductService;
import com.itcast.gmall.utils.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@RestController
public class ProductItemController {

	@Reference
	private ProductService productService;

	@Qualifier("mainThreadPoolExecutor")
	@Autowired
	private ThreadPoolExecutor mainThreadPoolExecutor;

	@Qualifier("otherThreadPoolExecutor")
	@Autowired
	private ThreadPoolExecutor otherThreadPoolExecutor;

	/**
	 * 查询商品信息详情
	 * @param id
	 * @return
	 */
	@GetMapping("/item/{id}.html")
	public CommonResult productInfo(@PathVariable("id") Long id) {
		//多线程执行任务
		CompletableFuture.supplyAsync(() -> {
			return "返回值";
		}, otherThreadPoolExecutor).whenComplete((result,exception)->{
			System.out.println("处理结果");
			System.out.println("处理异常");
		});

		EsProduct esProduct = productService.productAllInfo(id);
		return new CommonResult().success(esProduct);
	}

	/**
	 * 根据skuId查询商品信息
	 * @param id
	 * @return
	 */
	@GetMapping("/item/sku/{id}.html")
	public CommonResult productSkuInfo(@PathVariable("id") Long id) {
		EsProduct esProduct = productService.productSkuInfo(id);
		return new CommonResult().success(esProduct);
	}


}
