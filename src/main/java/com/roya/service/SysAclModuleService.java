package com.roya.service;

import com.google.common.base.Preconditions;
import com.roya.common.RequestHolder;
import com.roya.dao.SysAclMapper;
import com.roya.dao.SysAclModuleMapper;
import com.roya.exception.ParamException;
import com.roya.model.SysAclModule;
import com.roya.model.SysDept;
import com.roya.param.AclModuleParam;
import com.roya.utils.BeanValidator;
import com.roya.utils.IpUtil;
import com.roya.utils.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Created by idea
 * description :
 *
 * @author Loyail
 * @since 1.8JDK
 * CreateDate 2018-05-21-10:04
 */
@Service
public class SysAclModuleService {

	@Resource
	private SysAclModuleMapper aclModuleMapper;
	@Resource
	private SysAclMapper aclMapper;

	/**
	 * 权限模块新增
	 * @param param 权限模块参数值
	 */
	public void save(AclModuleParam param){
		BeanValidator.check(param);
		if (checkExist(param.getParentId(),param.getName(),param.getId())){
			throw new ParamException("同一层级下存在相同名称的权限模块");
		}
		SysAclModule aclModule = SysAclModule.builder()
				.name(param.getName()).parentId(param.getParentId())
				.status(param.getStatus()).remark(param.getRemark())
				.seq(param.getSeq()).build();

		aclModule.setLevel(LevelUtil.calculateLevel(getLevel(param.getParentId()),param.getParentId()));
		aclModule.setOperator(RequestHolder.getCurrentUser().getUsername());
		aclModule.setOperateTime(new Date());
		aclModule.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
		aclModuleMapper.insertSelective(aclModule);


	}


	/**
	 * 权限模块的更新操作
	 * @param param 权限模块参数值
	 */
	public void update(AclModuleParam param){
		BeanValidator.check(param);
		if (checkExist(param.getParentId(),param.getName(),param.getId())){
			throw new ParamException("同一层级下存在相同名称的权限模块");
		}
		//根据传入的id查找该对象
		SysAclModule before = aclModuleMapper.selectByPrimaryKey(param.getId());
		//判断这个权限模块是否存在
		Preconditions.checkNotNull(before,"待更新的权限模块不存在");
		SysAclModule after = SysAclModule.builder()
				.name(param.getName()).parentId(param.getParentId())
				.status(param.getStatus()).remark(param.getRemark())
				.seq(param.getSeq()).id(param.getId()).build();
		after.setLevel(LevelUtil.calculateLevel(getLevel(param.getParentId()),param.getParentId()));
		after.setOperator(RequestHolder.getCurrentUser().getUsername());
		after.setOperateTime(new Date());
		after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
		updateWithChild(before,after);
	}

	/**
	 * 更新层级（递归更新底层所有）
	 * @param before 更新之前
	 * @param after 更新之后
	 */
	@Transactional
	protected void updateWithChild(SysAclModule before, SysAclModule after){
//分别取出部门的先后前缀
		String newLevelPrefix = after.getLevel();
		String oldLevelPrefix = before.getLevel();

		if (!newLevelPrefix.equals(oldLevelPrefix)){
			List<SysAclModule> aclModuleList = aclModuleMapper.childListByLevel(before.getLevel());
			if (CollectionUtils.isNotEmpty(aclModuleList)){
				for (SysAclModule aclModule: aclModuleList){
					String level = aclModule.getLevel();
					if (level.indexOf(oldLevelPrefix) == 0){
						level = newLevelPrefix+level.substring(oldLevelPrefix.length());
						aclModule.setLevel(level);
					}
				}
				aclModuleMapper.batchUpdateLevel(aclModuleList);
			}
		}
		aclModuleMapper.updateByPrimaryKeySelective(after);
	}

	private boolean checkExist(Integer parentId, String aclModuleName, Integer aclModuleId){
		return  aclModuleMapper.countByNameAndParentId(parentId,aclModuleName,aclModuleId) > 0;
	}

	/**
	 * 获取当前操作的层级
	 * @param aclModuleId 权限模块的ID
	 * @return 层级值
	 */
	private String getLevel(Integer aclModuleId){
		SysAclModule aclModule = aclModuleMapper.selectByPrimaryKey(aclModuleId);
		if (null == aclModule){
			return null;
		}
		return aclModule.getLevel();
	}


	public void delete(int aclModuleId){
		//先判断一下部门树是否存在
		SysAclModule  aclModule = aclModuleMapper.selectByPrimaryKey(aclModuleId);
		Preconditions.checkNotNull(aclModule,"待删除的权限模块不存在，无法删除");

		if (aclModuleMapper.countByParentId(aclModule.getId()) > 0){
			throw new ParamException("当前权限模块下有子模块，无法删除");
		}
		if (aclMapper.countByAclModuleId(aclModule.getId()) > 0){
			throw new ParamException("当前权限模块下存在用户，无法删除");
		}
		aclModuleMapper.deleteByPrimaryKey(aclModuleId);
	}



}
