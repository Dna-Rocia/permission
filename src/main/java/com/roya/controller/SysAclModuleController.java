package com.roya.controller;

import com.roya.common.JsonData;
import com.roya.param.AclModuleParam;
import com.roya.service.SysAclModuleService;
import com.roya.service.SysTreeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

/**
 * Created by idea
 * description :
 *		权限模块管理
 * @author Loyail
 * @since 1.8JDK
 * CreateDate 2018-05-21-9:24
 */
@Controller
@RequestMapping("/sys/aclModule")
@Slf4j
public class SysAclModuleController {

	@Resource
	private SysAclModuleService aclModuleService;
	@Resource
	private SysTreeService sysTreeService;

	@RequestMapping("/acl.page")
	public ModelAndView page(){
		return  new ModelAndView("acl");
	}


	@RequestMapping("/save.json")
	@ResponseBody
	public JsonData saveAclModule(AclModuleParam aclModuleParam) {
		aclModuleService.save(aclModuleParam);
		return JsonData.success();
	}



	@RequestMapping("/update.json")
	@ResponseBody
	public JsonData updateAclModule(AclModuleParam aclModuleParam) {
		aclModuleService.update(aclModuleParam);
		return JsonData.success();
	}



	@RequestMapping("/tree.json")
	@ResponseBody
	public JsonData tree() {
		return JsonData.success(sysTreeService.aclModuleTree());
	}







}
