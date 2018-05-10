package com.roya.service;

import com.roya.dao.SysDeptMapper;
import com.roya.exception.ParamException;
import com.roya.model.SysDept;
import com.roya.param.DeptParam;
import com.roya.utils.BeanValidator;
import com.roya.utils.LevelUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Created by idea
 * description :
 *
 * @author Loyaill
 * @version 2.0.0
 * CreateDate 2018-05-10-13:25
 * @since 1.8JDK
 */
@Service
public class SysDeptService {
	@Resource
	private SysDeptMapper sysDeptMapper;

	public void save(DeptParam param){
		BeanValidator.check(param);
		if (checkExist(param.getParentId(),param.getName(),param.getId())){
			throw new ParamException("同一层级下存在相同名称的部门");
		}
		//创建实例
		SysDept sysDept = SysDept.builder().name(param.getName()).parentId(param.getParentId()).seq(param.getSeq()).remark(param.getRemark()).build();
		//实例的层级设置
		sysDept.setLevel(LevelUtil.calculateLevel(getLevel(param.getParentId()),param.getParentId()));
		sysDept.setOperator("system"); //todo
		sysDept.setOperateTime(new Date());
		sysDept.setOperateIp("127.0.0.1");
		int num = sysDeptMapper.insertSelective(sysDept);
		System.out.println(num);
	}

	/**
	 * 检查参数是否存在
	 * @param parentId 上一级部门id
	 * @param deptName 部门名称
	 * @param deptId 当前部门id
	 * @return 是否存在
	 */
	private boolean checkExist(Integer parentId, String deptName, Integer deptId){
		//todo
		return  true;
	}


	private String getLevel(Integer deptId){
		SysDept dept = sysDeptMapper.selectByPrimaryKey(deptId);
		if (null == dept){
			return null;
		}
		return dept.getLevel();
	}


}
