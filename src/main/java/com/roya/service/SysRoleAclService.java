package com.roya.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.roya.beans.LogType;
import com.roya.common.RequestHolder;
import com.roya.dao.SysLogMapper;
import com.roya.dao.SysRoleAclMapper;
import com.roya.model.SysLogWithBLOBs;
import com.roya.model.SysRoleAcl;
import com.roya.utils.IpUtil;
import com.roya.utils.JsonMapper;
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
 * @CreateTime 2018-07-21-14:38
 */
@Service
public class SysRoleAclService {

	@Resource
	private SysRoleAclMapper sysRoleAclMapper;
	@Resource
	private SysLogMapper sysLogMapper;

	public void changeRoleAcls(Integer roleId,List<Integer> aclIdList){
		//取出之前分配的权限
		List<Integer> originAclIdList = sysRoleAclMapper.getAclIdListByRoleIdList(Lists.newArrayList(roleId));
		//查看是否传进来的与之前的一样
		//第一步：长度相等有可能，数据是一样的
		if (originAclIdList.size() == aclIdList.size()){
			Set<Integer> originAclIdSet = Sets.newHashSet(originAclIdList);
			Set<Integer> aclIdSet = Sets.newHashSet(aclIdList);
			originAclIdSet.removeAll(aclIdSet);
			if (CollectionUtils.isEmpty(originAclIdSet)){
				return;
			}
		}
		updateRoleAcls(roleId,aclIdList);
		saveRoleAclLog(roleId,originAclIdList,aclIdList);
	}


	@Transactional
	public void updateRoleAcls(int roleId,List<Integer> aclIdList){
		sysRoleAclMapper.deleteByRoleId(roleId);

		if (CollectionUtils.isEmpty(aclIdList)){
			return;
		}

		List<SysRoleAcl> roleAclList = Lists.newArrayList();

		for (Integer aclId: aclIdList) {
			SysRoleAcl roleAcl = SysRoleAcl.builder().roleId(roleId).aclId(aclId).
					operator(RequestHolder.getCurrentUser().getUsername()).operateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest())).
					operateTime(new Date()).build();
			roleAclList.add(roleAcl);
		}
		sysRoleAclMapper.batchInsert(roleAclList);
	}

	private void  saveRoleAclLog(int roleId, List<Integer> before, List<Integer> after){
		SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
		sysLog.setType(LogType.TYPE_ROLE_ACL);
		sysLog.setTargetId(roleId);
		sysLog.setOldValue(before == null ? "" : JsonMapper.obj2String(before));
		sysLog.setNewValue(after == null ? "" : JsonMapper.obj2String(after));
		sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
		sysLog.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
		sysLog.setOperateTime(new Date());
		sysLog.setStatus(1);
		sysLogMapper.insertSelective(sysLog);
	}
}
