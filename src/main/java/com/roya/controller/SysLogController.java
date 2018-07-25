package com.roya.controller;

import com.roya.beans.PageQuery;
import com.roya.common.JsonData;
import com.roya.param.AclParam;
import com.roya.param.SearchLogParam;
import com.roya.service.SysLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

/**
 * @author Loyaill
 * @description :
 * @CreateTime 2018-07-25-11:39
 */
@Controller
@RequestMapping("/sys/log")
@Slf4j
public class SysLogController {

	@Resource
	private SysLogService sysLogService;


	@RequestMapping("/log.page")
	public ModelAndView index(){
		return  new ModelAndView("log");
	}



	@RequestMapping("/page.json")
	@ResponseBody
	public JsonData searchPage(SearchLogParam param, PageQuery pageQuery) {
		return JsonData.success(sysLogService.searchPageList(param,pageQuery));
	}


	@RequestMapping("/recover.json")
	@ResponseBody
	public JsonData recover(@RequestParam("id") int id) {
		return JsonData.success(sysLogService.searchPageList(param,pageQuery));
	}

}
