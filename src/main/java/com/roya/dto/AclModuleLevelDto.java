package com.roya.dto;

import com.google.common.collect.Lists;
import com.roya.model.SysAclModule;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * Created by idea
 * description :
 *		权限模块层级的适配
 * @author Loyail
 * @since 1.8JDK
 * CreateDate 2018-05-21-11:41
 */
@Getter
@Setter
@ToString
public class AclModuleLevelDto extends SysAclModule {

	//包含自己
	private List<AclModuleLevelDto> aclModuleList = Lists.newArrayList();

	public  static AclModuleLevelDto adapt(SysAclModule aclModule){
		AclModuleLevelDto levelDto = new AclModuleLevelDto();
		//将权限模块对象拷贝成所使用的dto
		BeanUtils.copyProperties(aclModule,levelDto);
		return  levelDto;
	}



}
