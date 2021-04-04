package com.itcast.gmall.admin.ums.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itcast.gmall.ums.entity.MemberLevel;
import com.itcast.gmall.ums.service.MemberLevelService;
import com.itcast.gmall.utils.CommonResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 会员等级表 前端控制器
 * </p>
 *
 * @author Pursuexy
 * @since 2021-02-26
 */
@CrossOrigin
@RestController
@RequestMapping
public class MemberLevelController {

	@Reference
	private MemberLevelService memberLevelService;

	@GetMapping("/member-level/list")
	public CommonResult memberLevelList() {
		List<MemberLevel> memberLevelList = memberLevelService.list();
		return new CommonResult().success(memberLevelList);
	}

}
