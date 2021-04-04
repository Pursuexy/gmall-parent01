package com.itcast.gmall.oms.component;

import com.alibaba.fastjson.JSON;
import com.itcast.gmall.constant.SystemCacheConstant;
import com.itcast.gmall.ums.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class MemberComponent {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	public Member getMemberByAccessToken(String accessToken) {
		String memberJson = stringRedisTemplate.opsForValue().get(SystemCacheConstant.LOGIN_MEMBER + accessToken);
		if (!StringUtils.isEmpty(memberJson)) {
			return JSON.parseObject(memberJson, Member.class);
		}
		return null;
	}
}

