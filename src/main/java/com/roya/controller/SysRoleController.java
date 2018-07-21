package com.roya.controller;

import com.roya.common.JsonData;
import com.roya.param.RoleParam;
import com.roya.service.SysRoleAclService;
import com.roya.service.SysRoleService;
import com.roya.service.SysRoleUserService;
import com.roya.service.SysTreeService;
import com.roya.utils.StringUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;

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
	@Resource
	private SysTreeService sysTreeService;
	@Resource
	private SysRoleAclService sysRoleAclService;
	@Resource
	private SysRoleUserService sysRoleUserService;

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

	@RequestMapping("/roleTree.json")
	@ResponseBody
	public  JsonData  roleTree(@RequestParam("roleId") int roleId){
		return JsonData.success(sysTreeService.roleTree(roleId));
	}

	@RequestMapping("/changeAcls.json")
	@ResponseBody
	public  JsonData  changeAcls(@RequestParam("roleId") int roleId,
								 @RequestParam(value = "aclIds",required = false,defaultValue = "")String aclIds){
		List<Integer> aclIdList = StringUtil.split2ListInt(aclIds);
		sysRoleAclService.changeRoleAcls(roleId,aclIdList);
		return JsonData.success();
	}

	@RequestMapping("/users.json")
	@ResponseBody
	public  JsonData  users(@RequestParam("roleId") int roleId){
		return JsonData.success(sysTreeService.roleTree(roleId));
	}


}
