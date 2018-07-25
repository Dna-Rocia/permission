package com.roya.service;

import com.google.common.base.Preconditions;
import com.roya.beans.PageQuery;
import com.roya.beans.PageResult;
import com.roya.common.RequestHolder;
import com.roya.dao.SysAclMapper;
import com.roya.exception.ParamException;
import com.roya.model.SysAcl;
import com.roya.param.AclParam;
import com.roya.utils.BeanValidator;
import com.roya.utils.IpUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by idea
 * description :
 *		权限
 * @author Loyail
 * @since 1.8JDK
 * CreateDate 2018-05-22-13:50
 */

@Service
public class SysAclService {
	@Resource
	private SysAclMapper aclMapper;
	@Resource
	private SysLogService sysLogService;

	public  void  save(AclParam  param){
		BeanValidator.check(param);
		if (checkExist(param.getAclModuleId(),param.getName(),param.getId())){
			throw new ParamException("当前权限模块下存在相同名称的权限点");
		}

		SysAcl acl = SysAcl.builder()
				.name(param.getName()).aclModuleId(param.getAclModuleId())
				.status(param.getStatus()).remark(param.getRemark())
				.seq(param.getSeq()).url(param.getUrl())
				.type(param.getType()).build();
		acl .setCode(generateCode());
		acl.setOperator(RequestHolder.getCurrentUser().getUsername());
		acl.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
		acl.setOperateTime(new Date());

		aclMapper.insertSelective(acl);
		sysLogService.saveAclLog(null,acl);

	}


	public  void  update(AclParam  param){
		BeanValidator.check(param);
		if (checkExist(param.getAclModuleId(),param.getName(),param.getId())){
			throw new ParamException("当前权限模块下存在相同名称的权限点");
		}
		SysAcl before = aclMapper.selectByPrimaryKey(param.getId());
		Preconditions.checkNotNull(before,"待更新的权限点不存在");

		SysAcl after = SysAcl.builder()
				.name(param.getName()).aclModuleId(param.getAclModuleId())
				.status(param.getStatus()).remark(param.getRemark())
				.seq(param.getSeq()).url(param.getUrl())
				.type(param.getType()).id(param.getId()).build();
		after .setCode(generateCode());
		after.setOperator(RequestHolder.getCurrentUser().getUsername());
		after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
		after.setOperateTime(new Date());

		aclMapper.updateByPrimaryKeySelective(after);
		sysLogService.saveAclLog(before,after);
	}

	/**
	 * 校验同一层级下是否存在相同名称的权限点
	 * @param aclModuleId 模块id
	 * @param aclName 权限点名称
	 * @param aclId 权限ID
	 * @return
	 */
	private boolean checkExist(Integer aclModuleId, String aclName, Integer aclId){
		return  aclMapper.countByNameAndAclModuleId(aclModuleId,aclName,aclId) > 0;
	}

	/**
	 * 自动生成Code
	 * @return code值
	 */
	private String generateCode(){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		return  dateFormat.format(new Date())+"_"+(int)(Math.random() * 100);
	}


	/**
	 * 权限点的list 分页查询
	 * @param aclModuleId 权限模块Id
	 * @param pageQuery  分页数据
	 * @return 返回分页数据
	 */
	public PageResult<SysAcl> listPageAcl(int aclModuleId, PageQuery pageQuery){
		BeanValidator.check(pageQuery);
		int count = aclMapper.countByAclModuleId(aclModuleId);

		if (count > 0){
			List<SysAcl> aclList = aclMapper.listPageAclByModuleId(aclModuleId,pageQuery);
			return PageResult.<SysAcl>builder().data(aclList).total(count).build();
		}
		return PageResult.<SysAcl>builder().build();
	}


}
