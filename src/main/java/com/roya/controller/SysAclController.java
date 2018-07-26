package com.roya.controller;

import com.google.common.collect.Maps;
import com.roya.beans.PageQuery;
import com.roya.common.JsonData;
import com.roya.model.SysRole;
import com.roya.param.AclParam;
import com.roya.service.SysAclService;
import com.roya.service.SysRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by idea
 * description :
 *		权限管理
 * @author Loyail
 * @since 1.8JDK
 * CreateDate 2018-05-21-9:47
 */
@Controller
@RequestMapping(value = "/sys/acl")
@Slf4j
public class SysAclController {
	@Resource
	private SysAclService aclService;
	@Resource
	private SysRoleService sysRoleService;


	@RequestMapping("/save.json")
	@ResponseBody
	public JsonData saveAcl(AclParam aclParam) {
		aclService.save(aclParam);
		return JsonData.success();
	}



	@RequestMapping("/update.json")
	@ResponseBody
	public JsonData updateAcl(AclParam aclParam) {
		aclService.update(aclParam);
		return JsonData.success();
	}


	@RequestMapping("/page.json")
	@ResponseBody
	public JsonData listPageAcl(@RequestParam("aclModuleId") int aclModuleId,PageQuery pageQuery) {
		return JsonData.success(aclService.listPageAcl(aclModuleId,pageQuery));
	}


	@RequestMapping("/acls.json")
	@ResponseBody
	public JsonData acls(@RequestParam("aclId") int aclId) {
		Map<String,Object> map = Maps.newHashMap();
		List<SysRole> roleList = sysRoleService.getRoleListByAclId(aclId);
		map.put("roles", roleList);
		map.put("users",sysRoleService.getUserListByRoleList(roleList));
		return JsonData.success(map);
	}


}
