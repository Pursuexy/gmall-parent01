package com.itcast.gmall.ums.service.impl;

import com.itcast.gmall.ums.entity.AdminLoginLog;
import com.itcast.gmall.ums.mapper.AdminLoginLogMapper;
import com.itcast.gmall.ums.service.AdminLoginLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 后台用户登录日志表 服务实现类
 * </p>
 *
 * @author Pursuexy
 * @since 2021-02-26
 */
@Service
@Component
public class AdminLoginLogServiceImpl extends ServiceImpl<AdminLoginLogMapper, AdminLoginLog> implements AdminLoginLogService {

}
