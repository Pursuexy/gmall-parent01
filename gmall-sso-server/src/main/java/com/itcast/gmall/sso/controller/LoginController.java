package com.itcast.gmall.sso.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.itcast.gmall.constant.SystemCacheConstant;
import com.itcast.gmall.ums.entity.Member;
import com.itcast.gmall.ums.service.MemberService;
import com.itcast.gmall.utils.CommonResult;
import com.itcast.gmall.vo.ums.LoginResponseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Reference
	private MemberService memberService;

	/**
	 * 查询用户详情
	 * @param accessToken
	 * @return
	 */
	@ResponseBody
	@GetMapping("/userInfo")
	public CommonResult getUserInfo(@RequestParam("accessToken") String accessToken) {
		String redisKey = SystemCacheConstant.LOGIN_MEMBER + accessToken;
		String member = stringRedisTemplate.opsForValue().get(redisKey);
		Member loginMember = JSON.parseObject(member, Member.class);
		//防止用户数据泄露，将关键数据值空
		loginMember.setId(null);
		loginMember.setPassword(null);
		return new CommonResult().success(loginMember);
	}

	/**
	 * 项目登录使用jwt令牌实现多端口的单点登录
	 * @param username
	 * @param password
	 * @return
	 */
	@ResponseBody
	@PostMapping("/appLogin")
	public CommonResult appLogin(@RequestParam("username") String username,
	                       @RequestParam("password") String password) {
		Member member = memberService.login(username, password);
		if (member == null) {
			//用户不存在，登录失败
			CommonResult commonResult = new CommonResult().failed();
			commonResult.setMessage("账号或者密码不匹配，请重新输入");
			return commonResult;
		}else{
			// 用户存在，登陆成功
			String token = UUID.randomUUID().toString().replace("-","");
			String memberJson = JSON.toJSONString(member);
			stringRedisTemplate.opsForValue().set(SystemCacheConstant.LOGIN_MEMBER+token,memberJson,
					SystemCacheConstant.LOGIN_MEMBER_TIMEOUT, TimeUnit.MINUTES);
			LoginResponseVo loginResponseVo = new LoginResponseVo();
			BeanUtils.copyProperties(member,loginResponseVo);
			//设置
			loginResponseVo.setAccessToken(token);
			return new CommonResult().success(loginResponseVo);
		}
	}

	@GetMapping("/login")
	public String login(@RequestParam(value = "redirect_url",required = false)
			            String redirect_url,
	                    @CookieValue(value = "sso_user",required = false)
			            String ssoUserCookie,
                        HttpServletResponse response,
                        Model model
	                    ) throws IOException {
		//判断是否登录过
		if (StringUtils.isEmpty(ssoUserCookie)) {
			//没登录过
			model.addAttribute("redirect_url", redirect_url);
			return "login";
		}else {
			//登陆过,返回原来登录页面
			String url = redirect_url + "?sso_user=" + ssoUserCookie;
			response.sendRedirect(url);
			return null;
		}
	}

	@PostMapping("/doLogin")
	public void doLogin(String username, String password,HttpServletResponse response,Model model) throws IOException {
		//模拟用户登录
		Map<String, Object> map = new HashMap<>();
		map.put("username", username);
		map.put("email", username + "@qq.com");

		//redis存储相关数据
		String token = UUID.randomUUID().toString().replace("-", "");
		stringRedisTemplate.opsForValue().set(token, JSON.toJSONString(map));

		//登陆成功，第一实现把用户信息写入token，第二实现调回原来链接的地址页面
		Cookie sso_user = new Cookie("sso_user", token);
		response.addCookie(sso_user);
		String redirect_url = (String) model.getAttribute("redirect_url");
		response.sendRedirect(redirect_url+"?sso_user="+sso_user);

	}
}
