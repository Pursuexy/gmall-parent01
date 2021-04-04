package com.itcast.gmall.ums.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itcast.gmall.ums.entity.Member;
import com.itcast.gmall.ums.entity.MemberReceiveAddress;
import com.itcast.gmall.ums.mapper.MemberMapper;
import com.itcast.gmall.ums.mapper.MemberReceiveAddressMapper;
import com.itcast.gmall.ums.service.MemberService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import java.util.List;

/**
 * <p>
 * 会员表 服务实现类
 * </p>
 *
 * @author Pursuexy
 * @since 2021-02-26
 */
@Service
@Component
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {

	@Autowired
	private MemberMapper memberMapper;

	@Autowired
	private MemberReceiveAddressMapper memberReceiveAddressMapper;

	/**
	 * 登录验证，并实现多端口单点登录
	 * @param username
	 * @param password
	 * @return
	 */
	@Override
	public Member login(String username, String password) {
		String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
		QueryWrapper<Member> queryWrapper = new QueryWrapper<Member>().eq("username", username).eq("password", md5Password);
		Member member = memberMapper.selectOne(queryWrapper);
		return member;
	}


	/**
	 * 根据AccessToken获取member信息
	 * @param accessToken
	 * @return
	 */
	@Override
	public Member getMemberByAccessToken(String accessToken) {
		return memberMapper.selectOne(new QueryWrapper<Member>().eq("access_token",accessToken));
	}

	/**
	 * 根据用户id获取用户地址信息
	 * @param id
	 * @return
	 */
	@Override
	public List<MemberReceiveAddress> getMemberAddress(Long id) {
		QueryWrapper<MemberReceiveAddress> queryWrapper = new QueryWrapper<MemberReceiveAddress>().eq("member_id", id);
		return memberReceiveAddressMapper.selectList(queryWrapper);
	}

	/**
	 * 根据memberId获取收货人的地址
	 * @param addressId
	 * @return
	 */
	@Override
	public MemberReceiveAddress getMemberAddressByAddressId(Long addressId) {
		return memberReceiveAddressMapper.selectById(addressId);
	}
}
