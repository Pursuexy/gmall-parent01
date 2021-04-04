package com.itcast.gmall.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itcast.gmall.search.ProductSearchService;
import com.itcast.gmall.vo.search.SearchParam;
import com.itcast.gmall.vo.search.SearchResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品检索controller
 */
@RestController
public class ProductSearchController {

	@Reference
	private ProductSearchService productSearchService;

	/**
	 * 检索商品
	 * @param searchParam
	 * @return
	 */
	@GetMapping("/search")
	public SearchResponse productSearchResponse(SearchParam searchParam) {
		SearchResponse searchResponse = productSearchService.searchProduct(searchParam);
		return searchResponse;
	}
}
