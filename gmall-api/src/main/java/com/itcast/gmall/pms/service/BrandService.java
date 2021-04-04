package com.itcast.gmall.pms.service;

import com.itcast.gmall.pms.entity.Brand;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itcast.gmall.utils.PageInfoVo;

/**
 * <p>
 * 品牌表 服务类
 * </p>
 *
 * @author Pursuexy
 * @since 2021-02-26
 */
public interface BrandService extends IService<Brand> {

	PageInfoVo brandPageInfo(String keyword, Integer pageNum, Integer pageSize);
}
