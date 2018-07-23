package com.roya.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.roya.common.JsonData;
import com.roya.model.SysUser;
import com.roya.param.RoleParam;
import com.roya.service.*;
import com.roya.utils.StringUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
	@Resource
	private SysUserService sysUserService;

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
		List<SysUser> selectedUserList = sysRoleUserService.getListByRoleId(roleId);
		List<SysUser> allUserList = sysUserService.getAll();
		List<SysUser> unSelectedUserList = Lists.newArrayList();
		//使用流式遍历
		Set<Integer> selectedUserIdSet = selectedUserList.stream().map(sysUser -> sysUser.getId()).collect(Collectors.toSet());
		for (SysUser user: allUserList) {
			if (user.getStatus() == 1 && !selectedUserIdSet.contains(user.getId())){
				unSelectedUserList.add(user);
			}
		}
		//过滤掉已选中，非1：正常的用户（不展示）
		//selectedUserList = selectedUserList.stream().filter(sysUser -> sysUser.getStatus() != 1).collect(Collectors.toList());
		//这里暂时不需要
		Map<String,List<SysUser>> map = Maps.newHashMap();
		map.put("selected",selectedUserList);
		map.put("unselected",unSelectedUserList);
		return JsonData.success(map);
	}

	@RequestMapping("/changeUsers.json")
	@ResponseBody
	public  JsonData  changeUsers(@RequestParam("roleId") int roleId,
								 @RequestParam(value = "userIds",required = false,defaultValue = "")String userIds){
		List<Integer> userIdList = StringUtil.split2ListInt(userIds);
		sysRoleUserService.changeRoleUsers(roleId,userIdList);
		return JsonData.success();
	}

}
