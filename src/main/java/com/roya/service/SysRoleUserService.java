package com.roya.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.roya.common.RequestHolder;
import com.roya.dao.SysRoleUserMapper;
import com.roya.dao.SysUserMapper;
import com.roya.model.SysRoleAcl;
import com.roya.model.SysRoleUser;
import com.roya.model.SysUser;
import com.roya.utils.IpUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;

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

	public void changeRoleUsers(int roleId,
								List<Integer> userIdList){
		//取出之前分配的用户
		List<Integer> originUserIdList = sysRoleUserMapper.getUserIdListByRoleId(roleId);
		//查看是否传进来的与之前的一样
		//第一步：长度相等有可能，数据是一样的
		if (originUserIdList.size() == userIdList.size()){
			Set<Integer> originUserIdSet = Sets.newHashSet(originUserIdList);
			Set<Integer> userIdSet = Sets.newHashSet(userIdList);
			originUserIdSet.removeAll(userIdSet);
			if (CollectionUtils.isEmpty(originUserIdSet)){
				return;
			}
		}
		updateRoleUsers(roleId,userIdList);
	}

	@Transactional
	public void updateRoleUsers(int roleId,
								 List<Integer> userIdList){
		sysRoleUserMapper.deleteByRoleId(roleId);

		if (CollectionUtils.isEmpty(userIdList)){
			return;
		}

		List<SysRoleUser> roleUserList = Lists.newArrayList();

		for (Integer userId: userIdList) {
			SysRoleUser roleAcl = SysRoleUser.builder().
					roleId(roleId).userId(userId).
					operator(RequestHolder.getCurrentUser().getUsername()).
					operateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest())).
					operateTime(new Date()).build();
			roleUserList.add(roleAcl);
		}
		sysRoleUserMapper.batchInsert(roleUserList);
	}
}
