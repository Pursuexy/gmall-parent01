package com.itcast.gmall.cart.entity;

import lombok.Data;

@Data
public class UserCartKey {

	private boolean loginStatus;//用户登录状态
	private Long userId;//用户Id
	private String finalCartKey;//用户最终使用的CartKey
}
