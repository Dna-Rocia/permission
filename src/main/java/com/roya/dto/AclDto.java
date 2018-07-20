package com.roya.dto;

import com.roya.model.SysAcl;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

/**
 * @author Loyaill
 * @description :
 * @CreateTime 2018-07-20-15:58
 */
@Getter
@Setter
@ToString
public class AclDto extends SysAcl {

	private boolean checked = false;//是否默认选中

	private boolean hasAcl = false; //是否有权限操作

	public static AclDto adapt(SysAcl acl){
		AclDto aclDto = new AclDto();
		BeanUtils.copyProperties(acl,aclDto);
		return aclDto;
	}
}
