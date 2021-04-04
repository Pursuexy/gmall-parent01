package com.itcast.gmall.cart.entity;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 购物项信息
 */
@Setter
public class CartItem implements Serializable {

	//购物项的基本信息
	@Getter
	private Long skuId;//skuId
	@Getter
	private String name;//商品名称
	@Getter
	private Long productId;
	@Getter
	private String skuCode;//sku编码
	@Getter
	private BigDecimal price;//价格
	@Getter
	private Integer stock;//库存
	@Getter
	private String sp1;//销售属性1
	@Getter
	private String sp2;//销售属性2
	@Getter
	private String sp3;//销售属性3
	@Getter
	private String pic;//展示图片
	@Getter
	private BigDecimal promotionPrice;//单品促销价格

	//购物项的其他信息
	@Getter
	private Integer count;//购买商品数量

	@Getter
	private boolean checkStatus=true;//当前购物项的选中状态

	private BigDecimal totalPrice;//当前购物项商品总价格

	public BigDecimal getTotalPrice() {
		BigDecimal multiply = price.multiply(new BigDecimal(count.toString()));
		return multiply;
	}
}
