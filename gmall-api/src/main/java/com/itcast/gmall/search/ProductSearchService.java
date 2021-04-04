package com.itcast.gmall.search;

import com.itcast.gmall.vo.search.SearchParam;
import com.itcast.gmall.vo.search.SearchResponse;

/**
 * 商品检索服务接口
 */
public interface ProductSearchService {

	/**
	 * 检索商品
	 * @param searchParam
	 * @return
	 */
	SearchResponse searchProduct(SearchParam searchParam);
}
