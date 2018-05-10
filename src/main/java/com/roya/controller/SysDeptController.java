package com.roya.controller;

import com.roya.common.JsonData;
import com.roya.param.DeptParam;
import com.roya.service.SysDeptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;


/**
 * Created by idea
 * description :
 *
 * @author Loyail
 * @version 2.0.0
 * CreateDate 2018-05-10-11:57
 * @since 1.8JDK
 */
@Controller
@RequestMapping("/sys/dept")
@Slf4j
public class SysDeptController {

	@Resource
	private SysDeptService deptService;

	@RequestMapping("/save.json")
	@ResponseBody
	public JsonData saveDept(DeptParam deptParam) {
		deptService.save(deptParam);
		return JsonData.success();
	}


}
