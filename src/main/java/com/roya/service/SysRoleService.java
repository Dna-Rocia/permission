package com.roya.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.roya.common.OperateHandle;
import com.roya.dao.SysRoleAclMapper;
import com.roya.dao.SysRoleMapper;
import com.roya.dao.SysRoleUserMapper;
import com.roya.dao.SysUserMapper;
import com.roya.exception.ParamException;
import com.roya.model.SysRole;
import com.roya.model.SysUser;
import com.roya.param.RoleParam;
import com.roya.utils.BeanValidator;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by idea
 * description :
 *
 * @author Loyail
 * @since 1.8JDK
 * CreateDate 2018-05-23-9:14
 */
@Service
public class SysRoleService {

	@Resource
	private SysRoleMapper sysRoleMapper;
	@Resource
	private SysRoleUserMapper sysRoleUserMapper;
	@Resource
	private SysRoleAclMapper sysRoleAclMapper;
	@Resource
	private SysUserMapper sysUserMapper;


	public  void  save(RoleParam param){
		BeanValidator.check(param);
		if (checkExist(param.getName(),param.getId())){
			throw new ParamException("角色名称已经存在");
		}
		SysRole role = SysRole.builder()
				.name(param.getName()).status(param.getStatus())
				.remark(param.getRemark()).type(param.getType())
				.build();
		OperateHandle.generateSetOperate(role);
		sysRoleMapper.insertSelective(role);
	}



	public  void  update(RoleParam param){
		BeanValidator.check(param);
		if (checkExist(param.getName(),param.getId())){
			throw new ParamException("角色名称已经存在");
		}
		SysRole before = sysRoleMapper.selectByPrimaryKey(param.getId());
		Preconditions.checkNotNull(before,"待更新的角色不存在");

		SysRole after = SysRole.builder()
				.name(param.getName()).status(param.getStatus())
				.remark(param.getRemark()).type(param.getType())
				.id(param.getId()).build();
		OperateHandle.generateSetOperate(after);

		sysRoleMapper.updateByPrimaryKeySelective(after);

	}


	private boolean checkExist(String roleName, Integer roleId){
		return  sysRoleMapper.countByName(roleName,roleId) > 0;
	}


	public List<SysRole> list(){
		return sysRoleMapper.list();
	}


	public List<SysRole> getRoleListByUserId(int userId){
		List<Integer> roleIdList = sysRoleUserMapper.getRoleIdListByUserId(userId);
		if (CollectionUtils.isEmpty(roleIdList)){
			return Lists.newArrayList();
		}
		return sysRoleMapper.getByIdList(roleIdList);
	}


	public List<SysRole> getRoleListByAclId(int aclId){
		List<Integer> roleIdList = sysRoleAclMapper.getRoleIdListByAclId(aclId);
		if (CollectionUtils.isEmpty(roleIdList)){
			return Lists.newArrayList();
		}
		return sysRoleMapper.getByIdList(roleIdList);
	}

	public List<SysUser> getUserListByRoleList(List<SysRole> roleList){
		if (CollectionUtils.isEmpty(roleList)){
			return Lists.newArrayList();
		}
		List<Integer> roleIdList = roleList.stream().map(sysRole -> sysRole.getId()).collect(Collectors.toList());
		List<Integer> userIdList = sysRoleUserMapper.getUserIdListByRoleIdList(roleIdList);
		if (CollectionUtils.isEmpty(userIdList)){
			return Lists.newArrayList();
		}
		return sysUserMapper.getByIdList(userIdList);
	}

}
