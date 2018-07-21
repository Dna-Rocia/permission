package com.roya.service;

import com.google.common.collect.Lists;
import com.roya.dao.SysRoleUserMapper;
import com.roya.dao.SysUserMapper;
import com.roya.model.SysUser;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Loyaill
 * @description :
 * @CreateTime 2018-07-21-14:56
 */
@Service
public class SysRoleUserService {

	@Resource
	private SysRoleUserMapper sysRoleUserMapper;
	@Resource
	private SysUserMapper sysUserMapper;


	public List<SysUser> getListByRoleId(int roleId){
		List<Integer> userIdList = sysRoleUserMapper.getUserIdListByRoleId(roleId);
		if (CollectionUtils.isEmpty(userIdList)){
			return Lists.newArrayList();
		}
		return sysUserMapper.getByIdList(userIdList);
	}



}
