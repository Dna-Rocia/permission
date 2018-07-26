package com.roya.service;

import com.google.common.base.Preconditions;
import com.roya.beans.LogType;
import com.roya.beans.PageQuery;
import com.roya.beans.PageResult;
import com.roya.common.RequestHolder;
import com.roya.dao.*;
import com.roya.dto.SearchLogDto;
import com.roya.exception.ParamException;
import com.roya.model.*;
import com.roya.param.SearchLogParam;
import com.roya.utils.BeanValidator;
import com.roya.utils.IpUtil;
import com.roya.utils.JsonMapper;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Loyaill
 * @description :
 * @CreateTime 2018-07-25-11:04
 */
@Service
public class SysLogService {

	@Resource
	private SysLogMapper sysLogMapper;
	@Resource
	private SysDeptMapper sysDeptMapper;
	@Resource
	private SysUserMapper sysUserMapper;
	@Resource
	private SysAclModuleMapper sysAclModuleMapper;
	@Resource
	private SysAclMapper sysAclMapper;
	@Resource
	private SysRoleMapper sysRoleMapper;
	@Resource
	private SysRoleAclService sysRoleAclService;
	@Resource
	private SysRoleUserService sysRoleUserService;


	public void recover(int id){
		SysLogWithBLOBs sysLog = sysLogMapper.selectByPrimaryKey(id);
		Preconditions.checkNotNull(sysLog,"待还原的记录不存在");
		switch (sysLog.getType()){
			case LogType.TYPE_DEPT:
				SysDept beforeDept = sysDeptMapper.selectByPrimaryKey(sysLog.getTargetId());
				Preconditions.checkNotNull(beforeDept,"待还原部门不存在，无法还原");
				if (StringUtils.isBlank(sysLog.getNewValue()) || StringUtils.isBlank(sysLog.getOldValue())){
					throw new ParamException("新增和删除操作不做还原");
				}
				SysDept afterDept = JsonMapper.stringToObject(sysLog.getOldValue(),new TypeReference<SysDept>(){}.getType());
				afterDept.setOperator(RequestHolder.getCurrentUser().getUsername());
				afterDept.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
				afterDept.setOperateTime(new Date());
				sysDeptMapper.updateByPrimaryKeySelective(afterDept);
				saveDeptLog(beforeDept,afterDept);
				break;
			case LogType.TYPE_USER:
				SysUser beforeUser = sysUserMapper.selectByPrimaryKey(sysLog.getTargetId());
				Preconditions.checkNotNull(beforeUser,"待还原用户不存在，无法还原");
				if (StringUtils.isBlank(sysLog.getNewValue()) || StringUtils.isBlank(sysLog.getOldValue())){
					throw new ParamException("新增和删除操作不做还原");
				}
				SysUser afterUser = JsonMapper.stringToObject(sysLog.getOldValue(),new TypeReference<SysUser>(){}.getType());
				afterUser.setOperator(RequestHolder.getCurrentUser().getUsername());
				afterUser.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
				afterUser.setOperateTime(new Date());
				sysUserMapper.updateByPrimaryKeySelective(afterUser);
				saveUserLog(beforeUser,afterUser);
				break;
			case LogType.TYPE_ACL_MODULE:
				SysAclModule beforeAclModule = sysAclModuleMapper.selectByPrimaryKey(sysLog.getTargetId());
				Preconditions.checkNotNull(beforeAclModule,"待还原权限模块不存在，无法还原");
				if (StringUtils.isBlank(sysLog.getNewValue()) || StringUtils.isBlank(sysLog.getOldValue())){
					throw new ParamException("新增和删除操作不做还原");
				}
				SysAclModule afterAclModule = JsonMapper.stringToObject(sysLog.getOldValue(),new TypeReference<SysAclModule>(){}.getType());
				afterAclModule.setOperator(RequestHolder.getCurrentUser().getUsername());
				afterAclModule.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
				afterAclModule.setOperateTime(new Date());
				sysAclModuleMapper.updateByPrimaryKeySelective(afterAclModule);
				saveAclModuleLog(beforeAclModule,afterAclModule);
				break;
			case LogType.TYPE_ACL:
				SysAcl beforeAcl = sysAclMapper.selectByPrimaryKey(sysLog.getTargetId());
				Preconditions.checkNotNull(beforeAcl,"待还原权限点不存在，无法还原");
				if (StringUtils.isBlank(sysLog.getNewValue()) || StringUtils.isBlank(sysLog.getOldValue())){
					throw new ParamException("新增和删除操作不做还原");
				}
				SysAcl afterAcl = JsonMapper.stringToObject(sysLog.getOldValue(),new TypeReference<SysAcl>(){}.getType());
				afterAcl.setOperator(RequestHolder.getCurrentUser().getUsername());
				afterAcl.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
				afterAcl.setOperateTime(new Date());
				sysAclMapper.updateByPrimaryKeySelective(afterAcl);
				saveAclLog(beforeAcl,afterAcl);
				break;
			case LogType.TYPE_ROLE:
				SysRole beforeRole = sysRoleMapper.selectByPrimaryKey(sysLog.getTargetId());
				Preconditions.checkNotNull(beforeRole,"待还原角色不存在，无法还原");
				if (StringUtils.isBlank(sysLog.getNewValue()) || StringUtils.isBlank(sysLog.getOldValue())){
					throw new ParamException("新增和删除操作不做还原");
				}
				SysRole afterRole = JsonMapper.stringToObject(sysLog.getOldValue(),new TypeReference<SysRole>(){}.getType());
				afterRole.setOperator(RequestHolder.getCurrentUser().getUsername());
				afterRole.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
				afterRole.setOperateTime(new Date());
				sysRoleMapper.updateByPrimaryKeySelective(afterRole);
				saveRoleLog(beforeRole,afterRole);
				break;
			case LogType.TYPE_ROLE_ACL:
				SysRole aclRole = sysRoleMapper.selectByPrimaryKey(sysLog.getTargetId());
				Preconditions.checkNotNull(aclRole,"角色不存在，无法还原");
				sysRoleAclService.changeRoleAcls(sysLog.getTargetId(),JsonMapper.stringToObject(sysLog.getOldValue(),
						new TypeReference<List<Integer>>(){}.getType()));
				break;
			case LogType.TYPE_ROLE_USER:
				SysRole userRole = sysRoleMapper.selectByPrimaryKey(sysLog.getTargetId());
				Preconditions.checkNotNull(userRole,"角色不存在，无法还原");
				sysRoleUserService.changeRoleUsers(sysLog.getTargetId(),JsonMapper.stringToObject(sysLog.getOldValue(),
						new TypeReference<List<Integer>>(){}.getType()));
				break;
			default:;
		}
	}

	public PageResult<SysLogWithBLOBs> searchPageList(SearchLogParam param, PageQuery page){
		BeanValidator.check(page);

		SearchLogDto dto = new SearchLogDto();
		dto.setType(param.getType());
		if (StringUtils.isNotBlank(param.getBeforeSeg())){
			dto.setBeforeSeg("%" +param.getBeforeSeg() + '%');
		}
		if (StringUtils.isNotBlank(param.getAfterSeg())){
			dto.setAfterSeg("%" +param.getAfterSeg() + '%');
		}
		if (StringUtils.isNotBlank(param.getOperator())){
			dto.setOperator("%" +param.getOperator() + '%');
		}
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			if (StringUtils.isNotBlank(param.getFromTime())){
				dto.setFromTime(format.parse(param.getFromTime()));
			}
			if (StringUtils.isNotBlank(param.getToTime())){
				dto.setToTime(format.parse(param.getToTime()));
			}
		}catch (Exception e){
			throw new ParamException("传入的日期格式有问题，正确格式为：yyyy-MM-dd hh:mm:ss");
		}
		int count = sysLogMapper.countBySearchDto(dto);
		if (count > 0){
			List<SysLogWithBLOBs> sysLogList = sysLogMapper.getPageListBySearchDto(dto,page);
			return PageResult.<SysLogWithBLOBs>builder().total(count).data(sysLogList).build();
		}
		return PageResult.<SysLogWithBLOBs>builder().build();
	}








	public void  saveDeptLog(SysDept before, SysDept after){
		SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
		sysLog.setType(LogType.TYPE_DEPT);
		sysLog.setTargetId(after == null ? before.getId() : after.getId());
		sysLog.setOldValue(before == null ? "" : JsonMapper.obj2String(before));
		sysLog.setNewValue(after == null ? "" : JsonMapper.obj2String(after));
		sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
		sysLog.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
		sysLog.setOperateTime(new Date());
		sysLog.setStatus(1);
		sysLogMapper.insertSelective(sysLog);
	}

	public void  saveUserLog(SysUser before, SysUser after){
		SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
		sysLog.setType(LogType.TYPE_USER);
		sysLog.setTargetId(after == null ? before.getId() : after.getId());
		sysLog.setOldValue(before == null ? "" : JsonMapper.obj2String(before));
		sysLog.setNewValue(after == null ? "" : JsonMapper.obj2String(after));
		sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
		sysLog.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
		sysLog.setOperateTime(new Date());
		sysLog.setStatus(1);
		sysLogMapper.insertSelective(sysLog);
	}

	public void  saveAclModuleLog(SysAclModule before, SysAclModule after){
		SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
		sysLog.setType(LogType.TYPE_ACL_MODULE);
		sysLog.setTargetId(after == null ? before.getId() : after.getId());
		sysLog.setOldValue(before == null ? "" : JsonMapper.obj2String(before));
		sysLog.setNewValue(after == null ? "" : JsonMapper.obj2String(after));
		sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
		sysLog.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
		sysLog.setOperateTime(new Date());
		sysLog.setStatus(1);
		sysLogMapper.insertSelective(sysLog);
	}

	public void  saveAclLog(SysAcl before, SysAcl after){
		SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
			sysLog.setType(LogType.TYPE_ACL);
		sysLog.setTargetId(after == null ? before.getId() : after.getId());
		sysLog.setOldValue(before == null ? "" : JsonMapper.obj2String(before));
		sysLog.setNewValue(after == null ? "" : JsonMapper.obj2String(after));
		sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
		sysLog.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
		sysLog.setOperateTime(new Date());
		sysLog.setStatus(1);
		sysLogMapper.insertSelective(sysLog);
	}

	public void  saveRoleLog(SysRole before, SysRole after){
		SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
		sysLog.setType(LogType.TYPE_ROLE);
		sysLog.setTargetId(after == null ? before.getId() : after.getId());
		sysLog.setOldValue(before == null ? "" : JsonMapper.obj2String(before));
		sysLog.setNewValue(after == null ? "" : JsonMapper.obj2String(after));
		sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
		sysLog.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
		sysLog.setOperateTime(new Date());
		sysLog.setStatus(1);
		sysLogMapper.insertSelective(sysLog);
	}


}
