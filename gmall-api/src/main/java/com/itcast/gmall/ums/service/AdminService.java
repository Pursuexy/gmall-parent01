package com.itcast.gmall.ums.service;

import com.itcast.gmall.ums.entity.Admin;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 后台用户表 服务类
 * </p>
 *
 * @author Pursuexy
 * @since 2021-02-26
 */
public interface AdminService extends IService<Admin> {

	Admin getUserInfo(String userName);

	Admin login(String username, String password);
}
