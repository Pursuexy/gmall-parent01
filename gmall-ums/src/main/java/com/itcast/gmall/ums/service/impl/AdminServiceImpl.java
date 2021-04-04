package com.itcast.gmall.ums.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itcast.gmall.ums.entity.Admin;
import com.itcast.gmall.ums.mapper.AdminMapper;
import com.itcast.gmall.ums.service.AdminService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

/**
 * <p>
 * 后台用户表 服务实现类
 * </p>
 *
 * @author Pursuexy
 * @since 2021-02-26
 */
@Component
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

	@Autowired
	private AdminMapper adminMapper;

	@Override
	public Admin getUserInfo(String userName) {
		return adminMapper.selectOne(new QueryWrapper<Admin>().eq("username",userName));
	}

	@Override
	public Admin login(String username, String password) {
		byte[] md5Password = DigestUtils.md5Digest(password.getBytes());
		QueryWrapper<Admin> queryWrapper = new QueryWrapper<Admin>().eq("username", username).eq("password",md5Password);
		Admin admin = adminMapper.selectOne(queryWrapper);
		return admin;
	}


}
