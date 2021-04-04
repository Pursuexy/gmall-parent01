package com.itcast.gmall.ums.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itcast.gmall.ums.entity.MemberLevel;
import com.itcast.gmall.ums.mapper.MemberLevelMapper;
import com.itcast.gmall.ums.service.MemberLevelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 会员等级表 服务实现类
 * </p>
 *
 * @author Pursuexy
 * @since 2021-02-26
 */
@Component
@Service
public class MemberLevelServiceImpl extends ServiceImpl<MemberLevelMapper, MemberLevel> implements MemberLevelService {

}
