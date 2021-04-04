package com.itcast.gmall.vo.ums;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class LoginResponseVo implements Serializable {

	private String username;//用户名

	private String nickname;//昵称

	private String phone;//手机号

	private String icon;//头像

	private Integer gender;//性别：0->未知；1->男；2->女

	private Date birthday;//生日

	private String city;//所做城市

	private String job;//职业

	private String personalizedSignature;//个性签名

	private Integer integration;//积分

	private Integer growth;//成长值

	private String accessToken;//访问令牌
}
