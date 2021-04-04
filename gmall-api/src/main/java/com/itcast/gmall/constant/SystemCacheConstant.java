package com.itcast.gmall.constant;

/**
 * 系统缓存常量
 */
public class SystemCacheConstant {

	//系统菜单
	public static final String CATEGORY_MENU_CACHE_KEY = "system_menu";
	//登录用户  login:member:token
	public static final String LOGIN_MEMBER = "login:member:";
	//登录用户过期时间(30分钟)
	public static final Long LOGIN_MEMBER_TIMEOUT = 30L;
	//订单的唯一检查的令牌，防重复下单令牌
	public static final String ORDER_UNIQUE_TOKEN = "order:unique:token";

}
