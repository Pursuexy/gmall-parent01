package com.itcast.gmall.ums.service.impl;

import com.itcast.gmall.ums.entity.Role;
import com.itcast.gmall.ums.mapper.RoleMapper;
import com.itcast.gmall.ums.service.RoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 后台用户角色表 服务实现类
 * </p>
 *
 * @author Pursuexy
 * @since 2021-02-26
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

}
