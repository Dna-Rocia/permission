package com.roya.common;

import com.roya.exception.ParamException;
import com.roya.exception.PermissionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by idea
 * description : 接口全局异常处理
 * @author Loyaill
 * @version 2.0.0
 * CreateDate 2018-05-09-11:31
 * @since 1.8JDK
 */
@Slf4j
public class SpringExceptionResolver implements HandlerExceptionResolver {

	public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
		String requestUrl = httpServletRequest.getRequestURL().toString();
		ModelAndView mv ;
		String defaultMsg = "System error";

		/**
		 * 	如何区分数据请求还是页面请求
		 * 	方法：
		 * 			①后缀区分（数据请求：.json,页面请求：.page ,.css etc）
		 * 			②从当前请求的handler 复杂
		 * 	下面使用方法一
		 */
		if (requestUrl.endsWith(".json")){ //数据请求
			if (e instanceof PermissionException || e instanceof ParamException){
				JsonData result = JsonData.fail(e.getMessage());
				mv = new ModelAndView("jsonView",result.objectMap());
			}else {
				log.error("unKnow JSON exception , url:" +requestUrl,e);
				JsonData result = JsonData.fail(defaultMsg);
				mv = new ModelAndView("jsonView",result.objectMap());
			}

		}else if (requestUrl.endsWith(".page")){//页面请求
			log.error("unKnow PAGE exception , url:" +requestUrl,e);
			JsonData result = JsonData.fail(defaultMsg);
			mv = new ModelAndView("exception",result.objectMap());
		}else {
			log.error("unKnow exception , url:" +requestUrl,e);
			JsonData result = JsonData.fail(defaultMsg);
			mv = new ModelAndView("jsonView",result.objectMap());
		}
		return mv;
	}
}
