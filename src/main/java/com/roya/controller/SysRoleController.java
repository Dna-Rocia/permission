package com.roya.controller;

import com.roya.common.JsonData;
import com.roya.param.RoleParam;
import com.roya.service.SysRoleService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

/**
 * Created by idea
 * description :
 *
 * @author Loyail
 * @since 1.8JDK
 * CreateDate 2018-05-23-9:14
 */
@Controller
@RequestMapping(value = "/sys/role")
public class SysRoleController {

	@Resource
	private SysRoleService sysRoleService;

	@RequestMapping("/role.page")
	public ModelAndView page(){
		return new ModelAndView("role");
	}


	@RequestMapping("/save.json")
	@ResponseBody
	public JsonData save(RoleParam param){
		sysRoleService.save(param);
		return JsonData.success();
	}

	@RequestMapping("/update.json")
	@ResponseBody
	public  JsonData  update(RoleParam param){
		sysRoleService.update(param);
		return JsonData.success();
	}

	@RequestMapping("/list.json")
	@ResponseBody
	public  JsonData  list(){
		return JsonData.success(sysRoleService.list());
	}

}
