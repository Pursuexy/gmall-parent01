package com.itcast.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itcast.gmall.pms.entity.Brand;
import com.itcast.gmall.pms.mapper.BrandMapper;
import com.itcast.gmall.pms.service.BrandService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itcast.gmall.utils.PageInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 品牌表 服务实现类
 * </p>
 *
 * @author Pursuexy
 * @since 2021-02-26
 */
@Service
@Component
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements BrandService {

	@Autowired
	private BrandMapper brandMapper;

	@Override
	public PageInfoVo brandPageInfo(String keyword, Integer pageNum, Integer pageSize) {
		QueryWrapper<Brand> queryWrapper = null;
		if (!StringUtils.isEmpty(keyword)) {
			queryWrapper = new QueryWrapper<Brand>().like("name", keyword);
		}
		IPage<Brand> page = brandMapper.selectPage(new Page<Brand>(pageNum.longValue(), pageSize.longValue()), queryWrapper);
		PageInfoVo pageInfoVo = new PageInfoVo(page.getTotal(), page.getPages(), pageSize.longValue(), page.getRecords(), pageNum.longValue());
		return pageInfoVo;
	}
}
