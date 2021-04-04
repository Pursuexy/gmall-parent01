package com.itcast.gmall.ums.service;

import com.itcast.gmall.ums.entity.Member;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itcast.gmall.ums.entity.MemberReceiveAddress;

import java.util.List;

/**
 * <p>
 * 会员表 服务类
 * </p>
 *
 * @author Pursuexy
 * @since 2021-02-26
 */
public interface MemberService extends IService<Member> {

	/**
	 * 登录验证，并实现多端口单点登录
	 * @param username
	 * @param password
	 * @return
	 */
	Member login(String username, String password);


	/**
	 * 根据AccessToken获取member信息
	 * @param accessToken
	 * @return
	 */
	Member getMemberByAccessToken(String accessToken);

	/**
	 * 根据用户id获取用户地址信息
	 * @param id
	 * @return
	 */
	List<MemberReceiveAddress> getMemberAddress(Long id);

	/**
	 * 根据memberId获取收货人的地址
	 * @param addressId
	 * @return
	 */
	MemberReceiveAddress getMemberAddressByAddressId(Long addressId);
}
