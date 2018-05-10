package com.roya.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.roya.dao.SysDeptMapper;
import com.roya.dto.DeptLevelDto;
import com.roya.model.SysDept;
import com.roya.utils.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by idea
 * description :
 *		整个项目有很多树，用于计算树结构
 * @author Loyaill
 * @since 1.8JDK
 * CreateDate 2018-05-10-14:16
 */
@Service
public class SysTreeService {

	@Resource
	private SysDeptMapper deptMapper;

	//计算部门树
	public List<DeptLevelDto>  deptTree (){
		List<SysDept> deptList = deptMapper.listDept();
		List<DeptLevelDto> deptLevelDtos = Lists.newArrayList();

		for (SysDept dept: deptList) {
			deptLevelDtos.add(DeptLevelDto.adapt(dept));
		}
		return deptList2Tree(deptLevelDtos);
	}

	//部门列表转成树
	public  List<DeptLevelDto> deptList2Tree(List<DeptLevelDto> deptLevelDtoList){
		if (CollectionUtils.isEmpty(deptLevelDtoList)){
			return Lists.newArrayList();
		}
		//level作为key —> [dept1,dept2,...部门list列表]作为value
		Multimap<String,DeptLevelDto> levelDeptMap = ArrayListMultimap.create();
		//（根目录）一级部门
		List<DeptLevelDto> rootList = Lists.newArrayList();
		for (DeptLevelDto dto: rootList
			 ) {
			levelDeptMap.put(dto.getLevel(),dto);
			if (LevelUtil.ROOT.equals(dto.getLevel())){
				rootList.add(dto);
			}
		}
		//对root（根目录）排序(从小到大)
		Collections.sort(rootList, comparator
//			new Comparator<DeptLevelDto>() {
//			public int compare(DeptLevelDto o1, DeptLevelDto o2) {
//				return o1.getSeq() -o2.getSeq();
//			}
//		}
		);
		//递归生成层级树
		transformDeptTree(rootList,LevelUtil.ROOT,levelDeptMap);
		return  rootList;
	}



	/**
	 * 将根目录下的子项进行递归排序
	 * @param deptLevelDtos 当前的部门结构
	 * @param level 当前的层级
	 * @param levelDeptMap 部门层级数据
	 */
	public void transformDeptTree(List<DeptLevelDto> deptLevelDtos, String level, Multimap<String,DeptLevelDto> levelDeptMap){

		for (int i = 0; i < deptLevelDtos.size(); i++) {
//			遍历每层各个元素
			DeptLevelDto dto = deptLevelDtos.get(i);
//			处理当前层级的数据
			String nextLevel = LevelUtil.calculateLevel(level,dto.getId());
//			处理下一层
			List<DeptLevelDto> tempDeptList =  (List<DeptLevelDto>)levelDeptMap.get(nextLevel);
			if (CollectionUtils.isNotEmpty(tempDeptList)){
				//排序
				Collections.sort(tempDeptList,comparator);
				//设置下一层部门
				dto.setDeptLevelDtos(tempDeptList);
				//进入下一层处理
				transformDeptTree(tempDeptList,nextLevel,levelDeptMap);
			}

		}

	}

		Comparator<DeptLevelDto> comparator = new Comparator<DeptLevelDto>() {
			public int compare(DeptLevelDto o1, DeptLevelDto o2) {
				return o1.getSeq() - o2.getSeq();
			}
		};

}
