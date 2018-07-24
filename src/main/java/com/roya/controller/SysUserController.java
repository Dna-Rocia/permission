package com.roya.controller;

import com.roya.beans.PageQuery;
import com.roya.beans.PageResult;
import com.roya.common.JsonData;
import com.roya.model.SysUser;
import com.roya.param.UserParam;
import com.roya.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

/**
 * Created by idea
 * description :
 *		系统 -- 用户管理
 * @author Loyail
 * @since 1.8JDK
 * CreateDate 2018-05-14-10:54
 */
@Controller
@RequestMapping("/sys/user")
@Slf4j
public class SysUserController {

	@Resource
	private SysUserService userService;

	@RequestMapping("/save.json")
	@ResponseBody
	public JsonData saveUser(UserParam userParam) {
		userService.insertUser(userParam);
		return JsonData.success();
	}


	@RequestMapping("/update.json")
	@ResponseBody
	public JsonData updateUser(UserParam userParam) {
		userService.updateUser(userParam);
		return JsonData.success();
	}



	@RequestMapping("/page.json")
	@ResponseBody
	public JsonData page(@RequestParam("deptId") int deptId, PageQuery pageQuery) {
		PageResult<SysUser> result = userService.getPageByDeptId(deptId,pageQuery);
		return JsonData.success(result);
	}




	@RequestMapping("/noAuth.page")
	public ModelAndView noAuth(){
		return  new ModelAndView("noAuth");
	}






}
