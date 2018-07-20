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
		return true;
	}

}
