package com.roya.service;

import com.roya.beans.LogType;
import com.roya.beans.PageQuery;
import com.roya.beans.PageResult;
import com.roya.common.RequestHolder;
import com.roya.dao.SysLogMapper;
import com.roya.dto.SearchLogDto;
import com.roya.exception.ParamException;
import com.roya.model.*;
import com.roya.param.SearchLogParam;
import com.roya.utils.BeanValidator;
import com.roya.utils.IpUtil;
import com.roya.utils.JsonMapper;
import org.apache.commons.lang3.StringUtils;
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

	public void recover(int id){
		SysLogWithBLOBs sysLog = sysLogMapper.selectByPrimaryKey(id);


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

	public void  saveRoleAclLog(int roleId, List<Integer> before, List<Integer> after){
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


	public void  saveRoleUserLog(int roleId, List<Integer> before, List<Integer> after){
		SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
		sysLog.setType(LogType.TYPE_ROLE_USER);
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
