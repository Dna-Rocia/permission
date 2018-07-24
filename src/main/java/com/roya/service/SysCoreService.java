package com.roya.service;

import com.google.common.collect.Lists;
import com.roya.common.RequestHolder;
import com.roya.dao.SysAclMapper;
import com.roya.dao.SysRoleAclMapper;
import com.roya.dao.SysRoleUserMapper;
import com.roya.model.SysAcl;
import com.roya.model.SysRoleAcl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Loyaill
 * @description :
 * @CreateTime 2018-07-20-16:21
 */
@Service
public class SysCoreService {

	@Resource
	private SysAclMapper sysAclMapper;
	@Resource
	private SysRoleUserMapper sysRoleUserMapper;
	@Resource
	private SysRoleAclMapper sysRoleAclMapper;


	public List<SysAcl> getCurrentUserAclList(){
		int userId = RequestHolder.getCurrentUser().getId();
		return getUserAclList(userId);
	}


	public List<SysAcl> getRoleAclList(int roleId){
		List<Integer> aclIdList =  sysRoleAclMapper.getAclIdListByRoleIdList(Lists.<Integer>newArrayList(roleId));
		if (CollectionUtils.isEmpty(aclIdList)){
			return Lists.newArrayList();
		}
		return sysAclMapper.getByIdList(aclIdList);
	}


	public List<SysAcl> getUserAclList(int userId){
		if (isSuperAdmin()){
			return  sysAclMapper.getAll();
		}
		List<Integer>  userRoleIdList = sysRoleUserMapper.getRoleIdListByUserId(userId);
		if (CollectionUtils.isEmpty(userRoleIdList)){
			return Lists.newArrayList();
		}
		List<Integer> userAclIdList = sysRoleAclMapper.getAclIdListByRoleIdList(userRoleIdList);
		if (CollectionUtils.isEmpty(userAclIdList)){
			return Lists.newArrayList();
		}
		return sysAclMapper.getByIdList(userAclIdList);
	}

	public boolean isSuperAdmin(){
		//todo
		return true;
	}



	public boolean hasUrlAcl(String url){
		if (isSuperAdmin()){
			return true;
		}
		//当权限点都不存在的时候，相当于在权限管理里不关心这个权限点，因此可以访问
		List<SysAcl> aclList = sysAclMapper.getByUrl(url);
		if (CollectionUtils.isEmpty(aclList)){
			return true;
		}
		//获取当前用户权限
		List<SysAcl> userAclList = getCurrentUserAclList();
		Set<Integer>  userAclIdSet = userAclList.stream().map(sysAcl -> sysAcl.getId()).collect(Collectors.toSet());

		boolean hasValidAcl = false;
		//url 只要有一个权限点有权限，那么我们就认为他有访问权限
		for (SysAcl acl : aclList) {
			// 判断一个用户是否具有某个权限点的访问权限   cl.getStatus() 非1- 无效
			if (acl == null || acl.getStatus() != 1){
				continue;
			}
			hasValidAcl = true;
			//当url校验有效的时候，才做出一下关联关系的判断
			if (userAclIdSet.contains(acl.getId())){
				return true;
			}
		}
		if (!hasValidAcl){
			return true;
		}
		//url 若没有权限访问的话，就返回false
		return false;
	}
}
