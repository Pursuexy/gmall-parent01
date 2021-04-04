package com.itcast.gmall.vo.product;

import com.itcast.gmall.pms.entity.ProductCategory;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 */
@Data
public class PmsProductCategoryWithChildrenItem extends ProductCategory implements Serializable {
    private List<ProductCategory> children;

}
