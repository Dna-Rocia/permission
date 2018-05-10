package com.roya.common;

import com.roya.utils.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by idea
 * description :
 *	Http请求前后的监听工具
 * @author Loyail
 * @version 2.0.0
 * CreateDate 2018-05-10-10:41
 * @since 1.8JDK
 */
@Slf4j
public class HttpInterceptor extends HandlerInterceptorAdapter{
	private static  String START_TIME = "requestStartTime";

	/**
	 * 处理之前的拦截
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String uri = request.getRequestURI().toString();
		Map map = request.getParameterMap();
		log.info("request start.  uri:{},params:{}",uri, JsonMapper.obj2String(map));
		long start = System.currentTimeMillis();
		request.setAttribute(START_TIME,start);
		return true;
	}

	/**
	 * 处理后的拦截，处理正常的请求
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//		String uri = request.getRequestURI().toString();
//		Map map = request.getParameterMap();
//		long startTime = (Long) request.getAttribute(START_TIME);
//		long endTime = System.currentTimeMillis();
//		log.info("request finished.  uri:{},params:{},costTime:{}",uri, JsonMapper.obj2String(map),endTime-startTime);
	}

	/**
	 * 处理后的拦截，正常的请求以及请求发生异常人正常的进行拦截
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		String uri = request.getRequestURI().toString();
		Map map = request.getParameterMap();
		long startTime = (Long) request.getAttribute(START_TIME);
		long endTime = System.currentTimeMillis();
		log.info("request completion.  uri:{},params:{},costTime:{}",uri, JsonMapper.obj2String(map),endTime-startTime);
	}
}
