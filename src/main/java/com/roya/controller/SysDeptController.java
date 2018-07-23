package com.roya.controller;

import com.roya.common.JsonData;
import com.roya.dto.DeptLevelDto;
import com.roya.param.DeptParam;
import com.roya.service.SysDeptService;
import com.roya.service.SysTreeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;


/**
 * Created by idea
 * description :
 *
 * @author Loyail
 * CreateDate 2018-05-10-11:57
 * @since 1.8JDK
 */
@Controller
@RequestMapping("/sys/dept")
@Slf4j
public class SysDeptController {

	@Resource
	private SysDeptService deptService;
	@Resource
	private SysTreeService sysTreeService;


	@RequestMapping("/dept.page")
	public ModelAndView page(){
		return  new ModelAndView("dept");
	}


	@RequestMapping("/save.json")
	@ResponseBody
	public JsonData saveDept(DeptParam deptParam) {
		deptService.save(deptParam);
		return JsonData.success();
	}


	@RequestMapping("/tree.json")
	@ResponseBody
	public JsonData tree() {
		List<DeptLevelDto> dtoList = sysTreeService.deptTree();
		return JsonData.success(dtoList);
	}


	@RequestMapping("/update.json")
	@ResponseBody
	public JsonData updateDept(DeptParam deptParam) {
		deptService.update(deptParam);
		return JsonData.success();
	}


	@RequestMapping("/delete.json")
	@ResponseBody
	public JsonData deleteDept(@RequestParam("id") int id) {
		deptService.delete(id);
		return JsonData.success();
	}
}
