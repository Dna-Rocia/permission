package com.roya.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by idea
 * description :
 *			管理员页面跳转控制
 * @author Loyail
 * @since 1.8JDK
 * CreateDate 2018-05-14-15:40
 */
@Controller
@RequestMapping("/admin")
public class AdminController {


	@RequestMapping("index.page")
	public ModelAndView index(){
		return  new ModelAndView("admin");
	}




}
