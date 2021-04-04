package com.itcast.gmall.cenilt.controller;

import com.itcast.gmall.cenilt.config.SsoServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class LoginController {

	@Autowired
	private SsoServerConfig ssoServerConfig;

	@GetMapping("/")
	public String login(Model model,
	                    @CookieValue(value = "sso_user",required = false)
	                    String ssoUserCookie,
	                    HttpServletRequest request,
	                    HttpServletResponse response) throws IOException {


		StringBuffer requestURL = request.getRequestURL();
		//判断是否登录
		if (StringUtils.isEmpty(ssoUserCookie)) {
			//没登录，重定向到ssoServer
			String url=ssoServerConfig.getUrl() + ssoServerConfig.getLoginPath() + "?redirect_url=" + requestURL.toString();
			// return "redirect:/" + url;
			response.sendRedirect(url);
			return null;
		}else{
			//登录成功
			model.addAttribute("loginUser", "张三");
			return "index";
		}
	}
}
