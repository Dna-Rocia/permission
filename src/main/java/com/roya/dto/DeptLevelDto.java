package com.roya.dto;

import com.google.common.collect.Lists;
import com.roya.model.SysDept;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * Created by idea
 * description :
 *		部门树的层级结构适配
 * @author Loyaill
 * @version 1
 * CreateDate 2018-05-10-14:05
 * @since 1.8JDK
 */
@Getter
@Setter
@ToString
public class DeptLevelDto extends SysDept{
	//包含自己
	private List<DeptLevelDto> deptLevelDtos = Lists.newArrayList();

	public  static DeptLevelDto adapt(SysDept dept){
		DeptLevelDto levelDto = new DeptLevelDto();
		//将部门对象拷贝成所使用的dto
		BeanUtils.copyProperties(dept,levelDto);
		return  levelDto;
	}




}
