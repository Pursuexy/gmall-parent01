package com.itcast.gmall.es.product;

import com.itcast.gmall.pms.entity.SkuStock;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class EsSkuProductInfo extends SkuStock implements Serializable {

	private String skuTitle;//sku的特定标题

	List<EsProductAttributeValue> esProductAttributeValues;//sku自身不同的销售属性以及其值
}
