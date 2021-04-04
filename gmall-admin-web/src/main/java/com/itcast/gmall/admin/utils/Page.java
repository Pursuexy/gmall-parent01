package com.itcast.gmall.admin.utils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel(value = "page", description = "数据分页")
public class Page<T> implements Serializable {
	@ApiModelProperty("数据页页码")
	private Integer pageNum;
	@ApiModelProperty("数据总条数")
	private Integer total;
	@ApiModelProperty("数据页页面大小")
	private Integer pageSize;
	@ApiModelProperty("数据内容")
	List<T> list = new ArrayList<>();
}
