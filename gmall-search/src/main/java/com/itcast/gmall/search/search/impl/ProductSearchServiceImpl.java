package com.itcast.gmall.search.search.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.itcast.gmall.constant.EsConstant;
import com.itcast.gmall.es.product.EsProduct;
import com.itcast.gmall.search.ProductSearchService;
import com.itcast.gmall.vo.search.SearchParam;
import com.itcast.gmall.vo.search.SearchResponse;
import com.itcast.gmall.vo.search.SearchResponseAttrVo;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.ChildrenAggregation;
import io.searchbox.core.search.aggregation.MetricAggregation;
import io.searchbox.core.search.aggregation.TermsAggregation;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

@Slf4j
@Component
@Service
public class ProductSearchServiceImpl implements ProductSearchService {

	@Autowired
	private JestClient jestClient;

	/**
	 * 检索商品
	 *
	 * @param searchParam
	 * @return
	 */
	@Override
	public SearchResponse searchProduct(SearchParam searchParam) {

		//1.构建检索条件
		String dsl = buildDsl(searchParam);
		log.error("商品检索的详细数据：{}", dsl);
		Search build = new Search.Builder(dsl).addIndex(EsConstant.PRODUCT_ES_INDEX).addType(EsConstant.PRODUCT_INFO_ES_TYPE).build();

		//2.进行检索查询
		SearchResult execute = null;
		try {
			execute = jestClient.execute(build);
		} catch (IOException e) {
			e.printStackTrace();
		}

		//3.将检索结果SearchResult封装成返回结果SearchResponse
		SearchResponse searchResponse = buildSearchResponse(execute);
		searchResponse.setPageNum(searchParam.getPageNum());
		searchResponse.setPageSize(searchParam.getPageSize());
		return searchResponse;
	}

	/**
	 * 封装检索商品查询结果数据到面包屑前端页面
	 *
	 * @param searchResult
	 * @return
	 */
	private SearchResponse buildSearchResponse(SearchResult searchResult) {
		SearchResponse searchResponse = new SearchResponse();
		MetricAggregation aggregations = searchResult.getAggregations();

		//1.封装可筛选的品牌
		TermsAggregation brand_agg = aggregations.getTermsAggregation("brand_agg");
		List<String> brandNames = new ArrayList<>();
		List<TermsAggregation.Entry> brandAggBuckets = brand_agg.getBuckets();
		brandAggBuckets.forEach((bucket) -> {
			String keyAsString = bucket.getKeyAsString();
			brandNames.add(keyAsString);
		});
		SearchResponseAttrVo attrVo = new SearchResponseAttrVo();
		attrVo.setName("品牌");
		attrVo.setValue(brandNames);
		searchResponse.setBrand(attrVo);

		//2.封装可筛选的分类
		TermsAggregation category_agg = aggregations.getTermsAggregation("category_agg");
		List<String> categoryNameAndIds = new ArrayList<>();
		List<TermsAggregation.Entry> categoryAggBuckets = category_agg.getBuckets();
		categoryAggBuckets.forEach((bucket)->{
			String categoryName = bucket.getKeyAsString();
			TermsAggregation categoryId_agg = bucket.getTermsAggregation("categoryId_agg");
			String categoryId = categoryId_agg.getBuckets().get(0).getKeyAsString();
			Map<String, String> map = new HashMap<>();
			map.put("categoryId", categoryId);
			map.put("categoryName", categoryName);
			String categoryInfo = JSON.toJSONString(map);
			categoryNameAndIds.add(categoryInfo);
		});
		SearchResponseAttrVo catelog = new SearchResponseAttrVo();
		catelog.setName("分类");
		catelog.setValue(categoryNameAndIds);
		searchResponse.setCatelog(catelog);


		//3.封装可筛选属性
		List<SearchResponseAttrVo> searchResponseAttrVos = new ArrayList<>();
		TermsAggregation attrNameAggregation = aggregations.getChildrenAggregation("attr_agg")
				.getTermsAggregation("attrName_agg");
		List<TermsAggregation.Entry> buckets = attrNameAggregation.getBuckets();
		buckets.forEach((bucket)->{
			SearchResponseAttrVo responseAttrVo = new SearchResponseAttrVo();
			//属性名称
			String attrName = bucket.getKeyAsString();
			responseAttrVo.setName(attrName);
			//属性id
			TermsAggregation attrIdAggregation = bucket.getTermsAggregation("attrId");
			String attrId = attrIdAggregation.getBuckets().get(0).getKeyAsString();
			responseAttrVo.setProductAttributeId(Long.parseLong(attrId));
			//属性值
			TermsAggregation attrValueAggregation = bucket.getTermsAggregation("attrValue");
			List<TermsAggregation.Entry> attrValueAggregationBuckets = attrValueAggregation.getBuckets();
			List<String> attrValueList = new ArrayList<>();
			attrValueAggregationBuckets.forEach((attrValueBucket)->{
				String attrValue = attrValueBucket.getKeyAsString();
				attrValueList.add(attrValue);
			});
			responseAttrVo.setValue(attrValueList);
		});
		searchResponse.setAttrs(searchResponseAttrVos);

		//4.封装检索出来的商品信息
		List<SearchResult.Hit<EsProduct, Void>> hits = searchResult.getHits(EsProduct.class);
		List<EsProduct> esProducts = new ArrayList<>();
		hits.forEach((hit)->{
			EsProduct source = hit.source;
			//注意替换高亮字段
			String skuTitle = hit.highlight.get("skuProductInfos.skuTitle").get(0);
			source.setSubTitle(skuTitle);
			esProducts.add(source);
		});
		searchResponse.setProducts(esProducts);

		//将以下封装直接在页面进来是进行分装
		//5.封装当前页面
		// searchResponse.setPageNum();
		//6.封装每页显示的内容
		// searchResponse.setPageSize();

		//7.封装总记录数
		searchResponse.setTotal(searchResult.getTotal());
		return searchResponse;
	}

	/**
	 * 构建dsl查询语句
	 *
	 * @param searchParam
	 * @return
	 */
	private String buildDsl(SearchParam searchParam) {
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

		//1.查询
		//1.1检索
		String keyword = searchParam.getKeyword();
		if (!StringUtils.isEmpty(keyword)) {
			MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("skuProductInfos.skuTitle", keyword);
			NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("skuProductInfos", matchQueryBuilder, ScoreMode.None);
			boolQueryBuilder.must(nestedQueryBuilder);
		}

		//1.2过滤
		//1.2.1  按照三级分类的条件过滤
		String[] catelog3 = searchParam.getCatelog3();
		if (catelog3.length > 0 && catelog3 != null) {

			boolQueryBuilder.filter(QueryBuilders.termsQuery("productCategoryId", catelog3));
		}

		//1.2.2  按照品牌过滤
		String[] brand = searchParam.getBrand();
		if (brand.length > 0 && brand != null) {
			boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId", brand));
		}

		//1.2.3  按照属性过滤
		String[] props = searchParam.getProps();
		if (props != null & props.length > 0) {
			for (String prop : props) {
				//2:4G-3G  2号属性的属性值为4G或者3G
				String[] split = prop.split(":");
				BoolQueryBuilder must = QueryBuilders.boolQuery()
						.must(QueryBuilders.matchQuery("attrValueList.productAttributeId", split[0]))
						.must(QueryBuilders.termsQuery("attrValueList.value", split[1].split("-")));
				NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrValueList", must, ScoreMode.None);
				boolQueryBuilder.filter(nestedQueryBuilder);
			}
		}

		//1.2.4  按照价格区间过滤
		Integer priceFrom = searchParam.getPriceFrom();
		Integer priceTo = searchParam.getPriceTo();
		if (priceFrom != null || priceTo != null) {
			RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("price");
			if (priceFrom != null) {
				rangeQueryBuilder.from(priceFrom);
			}
			if (priceTo != null) {
				rangeQueryBuilder.to(priceTo);
			}
			boolQueryBuilder.filter(rangeQueryBuilder);
		}
		sourceBuilder.query(boolQueryBuilder);

		//2.聚合
		//2.1  按照品牌聚合
		TermsAggregationBuilder brandAggregationBuilder = AggregationBuilders.terms("brand_agg").field("brandName.keyword");
		brandAggregationBuilder.subAggregation(AggregationBuilders.terms("brandId_agg").field("brandId"));
		sourceBuilder.aggregation(brandAggregationBuilder);
		//2.2 按照分类聚合
		TermsAggregationBuilder categoryAggregationBuilder = AggregationBuilders.terms("category_agg").field("productCategoryName.keyword");
		categoryAggregationBuilder.subAggregation(AggregationBuilders.terms("categoryId_agg").field("productCategoryId"));
		sourceBuilder.aggregation(categoryAggregationBuilder);
		//2.3  按照属性聚合
		NestedAggregationBuilder nestedAttrAggregationBuilder = AggregationBuilders.nested("attr_agg", "attrValueList");
		//子聚合-------------看属性名称
		TermsAggregationBuilder attrNameAggregationBuilder = AggregationBuilders.terms("attrName_agg").field("attrValueList.name");
		//在进一步子聚合
		//进一步子聚合-------------看属性值
		TermsAggregationBuilder attrValueAggregationBuilder = AggregationBuilders.terms("attrValue_agg").field("attrValueList.value.keyword");
		attrNameAggregationBuilder.subAggregation(attrValueAggregationBuilder);
		//进一步子聚合-------------看属性id
		TermsAggregationBuilder attrIdAggregationBuilder = AggregationBuilders.terms("attrId_agg").field("attrValueList.id");
		attrNameAggregationBuilder.subAggregation(attrIdAggregationBuilder);
		nestedAttrAggregationBuilder.subAggregation(attrNameAggregationBuilder);
		sourceBuilder.aggregation(nestedAttrAggregationBuilder);

		//3.分页
		Integer pageNum = searchParam.getPageNum();
		Integer pageSize = searchParam.getPageSize();
		sourceBuilder.from((pageNum - 1) * pageSize);
		sourceBuilder.size(pageSize);

		//4.高亮
		if (!StringUtils.isEmpty(keyword)) {
			HighlightBuilder highlightBuilder = new HighlightBuilder().field("skuProductInfos.skuTitle").preTags("<b style='color:red'>").postTags("</b>");
			sourceBuilder.highlighter(highlightBuilder);
		}

		//5.排序
		String order = searchParam.getOrder();
		//order=0:desc
		if (!StringUtils.isEmpty(order)) {
			String[] split = order.split(":");
			if (split[0].equals("0")) {
				//0：综合排序，默认顺序
			}
			if (split[0].equals("1")) {
				//1：销量排序
				FieldSortBuilder fieldSortBuilder = SortBuilders.fieldSort("sale");
				if (split[1].equalsIgnoreCase("desc")) {
					fieldSortBuilder.order(SortOrder.DESC);
				} else {
					fieldSortBuilder.order(SortOrder.ASC);
				}
				sourceBuilder.sort(fieldSortBuilder);
			}
			if (split[0].equals("2")) {
				//1：销量排序
				FieldSortBuilder fieldSortBuilder = SortBuilders.fieldSort("price");
				if (split[1].equalsIgnoreCase("desc")) {
					fieldSortBuilder.order(SortOrder.DESC);
				} else {
					fieldSortBuilder.order(SortOrder.ASC);
				}
				sourceBuilder.sort(fieldSortBuilder);
			}
		}
		return sourceBuilder.toString();
	}
}
