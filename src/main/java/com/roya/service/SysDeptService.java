package com.roya.service;

import com.google.common.base.Preconditions;
import com.roya.common.RequestHolder;
import com.roya.dao.SysDeptMapper;
import com.roya.exception.ParamException;
import com.roya.model.SysDept;
import com.roya.param.DeptParam;
import com.roya.utils.BeanValidator;
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
		sysDept.setOperator(RequestHolder.getCurrentUser().getUsername());
		sysDept.setOperateTime(new Date());
		sysDept.setOperateIp("127.0.0.1"); //todo
		int num = sysDeptMapper.insertSelective(sysDept);
		System.out.println(num);
	}

	/**
	 * 检查参数是否存在/重复
	 * @param parentId 上一级部门id
	 * @param deptName 部门名称
	 * @param deptId 当前部门id
	 * @return 是否存在
	 */
	private boolean checkExist(Integer parentId, String deptName, Integer deptId){
		return  sysDeptMapper.countByNameAndParentId(parentId,deptName,deptId) > 0;
	}


	private String getLevel(Integer deptId){
		SysDept dept = sysDeptMapper.selectByPrimaryKey(deptId);
		if (null == dept){
			return null;
		}
		return dept.getLevel();
	}


	/**
	 * 更新部门树
	 * @param param
	 */
	public void update(DeptParam param){
		//先处理当前的参数校验
		BeanValidator.check(param);
		if (checkExist(param.getParentId(),param.getName(),param.getId())){
			throw new ParamException("同一层级下存在相同名称的部门");
		}

		SysDept before = sysDeptMapper.selectByPrimaryKey(param.getId());
		Preconditions.checkNotNull(before,"待更新的部门不存在");

		if (checkExist(param.getParentId(),param.getName(),param.getId())){
			throw new ParamException("同一层级下存在相同名称的部门");
		}
		SysDept after = SysDept.builder().id(param.getId()).name(param.getName()).parentId(param.getParentId()).seq(param.getSeq()).remark(param.getRemark()).build();
		after.setLevel(LevelUtil.calculateLevel(getLevel(param.getParentId()),param.getParentId()));

		after.setOperator(RequestHolder.getCurrentUser().getUsername());
		after.setOperateIp("127.0.0.1");//todo
		after.setOperateTime(new Date());

		updateWithChild(before,after);
	}

	/**
	 * 更新子项
	 * @param before 更新前的部门
	 * @param after 更新后的部门
	 */
	@Transactional
	public void updateWithChild(SysDept before, SysDept after){
		//分别取出部门的先后前缀
		String newLevelPrefix = after.getLevel();
		String oldLevelPrefix = before.getLevel();

		if (!newLevelPrefix.equals(oldLevelPrefix)){
			List<SysDept> deptList = sysDeptMapper.childListByLevel(before.getLevel());
			if (CollectionUtils.isNotEmpty(deptList)){
				for (SysDept dept: deptList){
					String level = dept.getLevel();
					if (level.indexOf(oldLevelPrefix) == 0){
						level = newLevelPrefix+level.substring(oldLevelPrefix.length());
						dept.setLevel(level);
					}
				}
				sysDeptMapper.batchUpdateLevel(deptList);
			}
		}
		sysDeptMapper.updateByPrimaryKey(after);
	}


}
