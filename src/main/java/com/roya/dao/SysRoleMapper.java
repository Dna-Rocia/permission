package com.roya.dao;

import com.roya.model.SysRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysRoleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysRole record);

    int insertSelective(SysRole record);

    SysRole selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysRole record);

    int updateByPrimaryKey(SysRole record);

	List<SysRole> list();

	int countByName(@Param("name") String name ,@Param("id") Integer id);

	//List<Integer> getAclIdListByRoleIdList(@Param("roleIdList") List<Integer> roleIdList);

	List<SysRole> getByIdList(@Param("IdList") List<Integer> idList);

}