package com.itcast.gmall.cart.component;

import com.alibaba.fastjson.JSON;
import com.itcast.gmall.cart.entity.CartConstant;
import com.itcast.gmall.cart.entity.UserCartKey;
import com.itcast.gmall.constant.SystemCacheConstant;
import com.itcast.gmall.ums.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Component
public class MemberComponent {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	/**
	 * 根据AccessToken获取
	 * @param accessToken
	 * @return
	 */
	public Member getMemberByAccessToken(String accessToken) {
		String userJson = stringRedisTemplate.opsForValue().get(SystemCacheConstant.LOGIN_MEMBER + accessToken);
		Member member = JSON.parseObject(userJson, Member.class);
		return member;
	}

	/**
	 * 获取cartKey
	 * @param cartKey
	 * @param accessToken
	 * @return
	 */
	public UserCartKey getCartKey(String cartKey,String accessToken) {
		UserCartKey userCartKey = new UserCartKey();
		Member member = null;
		if (!StringUtils.isEmpty(accessToken)) {
			member=getMemberByAccessToken(accessToken);
		}
		if (member != null) {
			//1.用户登录，购物车用在线购物车，cart：user：skuId
			userCartKey.setLoginStatus(true);
			userCartKey.setFinalCartKey(CartConstant.USER_CART_KEY_PREFIX + member.getId());
			userCartKey.setUserId(member.getId());
			return userCartKey;
		} else if (!StringUtils.isEmpty(cartKey)) {
			//2.没登录，使用浏览器临时购物车，cart：temp：cartKey
			userCartKey.setLoginStatus(false);
			userCartKey.setFinalCartKey(CartConstant.TEMP_CART_KEY_PREFIX + cartKey);
			return userCartKey;
		}else {
			//3.参数没完整提供，自动分配临时购物车
			cartKey = UUID.randomUUID().toString().replace("-", "");
			userCartKey.setLoginStatus(false);
			userCartKey.setFinalCartKey(CartConstant.TEMP_CART_KEY_PREFIX + cartKey);
			return userCartKey;
		}
	}

}
