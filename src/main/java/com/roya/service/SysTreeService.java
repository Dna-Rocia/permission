package com.roya.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.roya.dao.SysAclMapper;
import com.roya.dao.SysAclModuleMapper;
import com.roya.dao.SysDeptMapper;
import com.roya.dto.AclDto;
import com.roya.dto.AclModuleLevelDto;
import com.roya.dto.DeptLevelDto;
import com.roya.model.SysAcl;
import com.roya.model.SysAclModule;
import com.roya.model.SysDept;
import com.roya.utils.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by idea
 * description :
 *		整个项目有很多树，用于计算树结构
 * @author Loyail
 * @since 1.8JDK
 * CreateDate 2018-05-10-14:16
 */
@Service
public class SysTreeService {

	@Resource
	private SysDeptMapper deptMapper;
	@Resource
	private SysAclModuleMapper aclModuleMapper;
	@Resource
	private SysCoreService sysCoreService;
	@Autowired
	private SysAclMapper sysAclMapper;



	//region  查询指定用户对应的权限树

	public List<AclModuleLevelDto> userAclTree(int userId){
		//指定用户已分配的权限点
		List<SysAcl> userAclList = sysCoreService.getUserAclList(userId);
		//当前系统所有的权限点
		List<AclDto> aclDtoList = Lists.newArrayList();

		for (SysAcl acl : userAclList) {
			AclDto dto = AclDto.adapt(acl);
			dto.setHasAcl(true);
			dto.setChecked(true);
			aclDtoList.add(dto);
		}
		return aclList2Tree(aclDtoList);
	}



	//endregion



	//region 角色对应的权限树
	public List<AclModuleLevelDto> roleTree(int roleId){
		//当前用户已分配的权限点
		List<SysAcl> userAclList = sysCoreService.getCurrentUserAclList();
		//当前角色分配的权限点
		List<SysAcl> roleAclList = sysCoreService.getRoleAclList(roleId);
		//当前系统所有的权限点
		List<AclDto> aclDtoList = Lists.newArrayList();

		Set<Integer> userAclIdSet = userAclList.stream().map(sysAcl -> sysAcl.getId()).collect(Collectors.toSet());
		Set<Integer> roleAclIdSet = roleAclList.stream().map(sysAcl -> sysAcl.getId()).collect(Collectors.toSet());
		//合成并集
	//	Set<SysAcl> aclSet = new HashSet<>(allAclList);
//		aclSet.addAll(userAclList);
		//取出所有的权限点
		List<SysAcl> allAclList = sysAclMapper.getAll();
		for (SysAcl acl : allAclList) {
			AclDto dto = AclDto.adapt(acl);
			if (userAclIdSet.contains(acl.getId())){
				dto.setHasAcl(true);
			}
			if (roleAclIdSet.contains(acl.getId())){
				dto.setChecked(true);
			}
			aclDtoList.add(dto);
		}
		return aclList2Tree(aclDtoList);
	}



	public List<AclModuleLevelDto>  aclList2Tree(List<AclDto> aclDtoList){
		if (CollectionUtils.isEmpty(aclDtoList)){
			return 	Lists.newArrayList();
		}
		List<AclModuleLevelDto> aclModuleLevelList = aclModuleTree();
		Multimap<Integer,AclDto> moduleIdAclMap = ArrayListMultimap.create();
		for (AclDto acl : aclDtoList){
			if(acl.getStatus()  == 1 ){ //状态为正常
				moduleIdAclMap.put(acl.getAclModuleId(),acl);
			}
		}
		bindAclsWithOrder(aclModuleLevelList,moduleIdAclMap);
		return aclModuleLevelList;
	}

	/**
	 * 递归将权限点绑定到模块树上面
	 */
	private void bindAclsWithOrder(List<AclModuleLevelDto> aclModuleLevelList,Multimap<Integer,AclDto> moduleIdAclMap){
		if (CollectionUtils.isEmpty(aclModuleLevelList)){
			return ;
		}
		for (AclModuleLevelDto dto : aclModuleLevelList){
			List<AclDto> aclDtoList =  (List<AclDto>)moduleIdAclMap.get(dto.getId());
			if(CollectionUtils.isNotEmpty(aclDtoList)){
				Collections.sort(aclDtoList,aclDtoComparator);
				dto.setAclList(aclDtoList);
			}
			bindAclsWithOrder(dto.getAclModuleList(),moduleIdAclMap);
		}
	}

	//权限树的层级比较器
	public 	Comparator<AclDto> aclDtoComparator = new Comparator<AclDto>() {
		public int compare(AclDto o1, AclDto o2) {
			return o1.getSeq() - o2.getSeq();
		}
	};




	//endregion




	//region 权限模块树的计算
	public List<AclModuleLevelDto>  aclModuleTree (){
		List<SysAclModule> aclModuleList = aclModuleMapper.getAllAclModule();

		List<AclModuleLevelDto>  aclModuleLevelDtos= Lists.newArrayList();

		for (SysAclModule aclModule: aclModuleList) {
			aclModuleLevelDtos.add(AclModuleLevelDto.adapt(aclModule));
		}
		return aclModuleList2Tree(aclModuleLevelDtos);
	}

	private List<AclModuleLevelDto> aclModuleList2Tree(List<AclModuleLevelDto>  aclModuleLevelDtos){
		if (CollectionUtils.isEmpty(aclModuleLevelDtos)){
			return Lists.newArrayList();
		}
		//level作为key —> [aclModule1,aclModule2,...权限模块list列表]作为value ,相当于Map<String,List<Object>>
		Multimap<String,AclModuleLevelDto> levelAclModuleMap = ArrayListMultimap.create();

		//（根目录）一级权限模块
		List<AclModuleLevelDto> rootList = Lists.newArrayList();
		for (AclModuleLevelDto dto: aclModuleLevelDtos) {
			levelAclModuleMap.put(dto.getLevel(),dto);
			if (LevelUtil.ROOT.equals(dto.getLevel())){
				rootList.add(dto);
			}
		}
		//对root（根目录）排序(从小到大)
		Collections.sort(rootList,aclModuleLevelDtoComparator);

		//递归生成层级树
		transformAclModuleTree(rootList,LevelUtil.ROOT,levelAclModuleMap);
		return  rootList;
	}

	//权限模块树的层级比较器
	public  Comparator<AclModuleLevelDto> aclModuleLevelDtoComparator= new Comparator<AclModuleLevelDto>() {
		public int compare(AclModuleLevelDto o1, AclModuleLevelDto o2) {
			return o1.getSeq() - o2.getSeq();
		}
	};

	/**
	 * 遍历权限模块数据，生成相对应的树结构
	 * @param dtoList （下一层） 模块数据
	 * @param level （下一层）层级
	 * @param levelAclModuleMap 所有数据
	 */
	public void transformAclModuleTree (List<AclModuleLevelDto> dtoList,String level, Multimap<String,AclModuleLevelDto> levelAclModuleMap){
		for (int i = 0; i < dtoList.size(); i++) {
//			遍历每层各个元素
			AclModuleLevelDto dto = dtoList.get(i);
//			处理当前层级的数据
			String nextLevel = LevelUtil.calculateLevel(level,dto.getId());
//			处理下一层
			List<AclModuleLevelDto> tempAclModuleList =  (List<AclModuleLevelDto>)levelAclModuleMap.get(nextLevel);
			if (CollectionUtils.isNotEmpty(tempAclModuleList)){
				//排序
				Collections.sort(tempAclModuleList,aclModuleLevelDtoComparator);
				//设置下一层部门
				dto.setAclModuleList(tempAclModuleList);
				//进入下一层处理
				transformAclModuleTree(tempAclModuleList,nextLevel,levelAclModuleMap);
			}
		}
	}



	//endregion









	//region   部门树的计算
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
		for (DeptLevelDto dto: deptLevelDtoList) {
			levelDeptMap.put(dto.getLevel(),dto);
			if (LevelUtil.ROOT.equals(dto.getLevel())){
				rootList.add(dto);
			}
		}
		//对root（根目录）排序(从小到大)
		Collections.sort(rootList,deptLevelDtoComparator);
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
				Collections.sort(tempDeptList,deptLevelDtoComparator);
				//设置下一层部门
				dto.setDeptList(tempDeptList);
				//进入下一层处理
				transformDeptTree(tempDeptList,nextLevel,levelDeptMap);
			}

		}

	}

	//部门树的层级比较器
	public 	Comparator<DeptLevelDto> deptLevelDtoComparator = new Comparator<DeptLevelDto>() {
			public int compare(DeptLevelDto o1, DeptLevelDto o2) {
				return o1.getSeq() - o2.getSeq();
			}
		};

	//endregion


}
