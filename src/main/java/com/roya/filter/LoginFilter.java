package com.roya.filter;

import com.roya.common.RequestHolder;
import com.roya.model.SysUser;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by idea
 * description :
 * 		判断过滤拦截用户是否登录
 * @author Loyail
 * @since 1.8JDK
 * CreateDate 2018-05-16-13:52
 */
@Slf4j
public class LoginFilter implements Filter{


	public void init(FilterConfig filterConfig) throws ServletException {

	}

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest)servletRequest;
		HttpServletResponse response = (HttpServletResponse)servletResponse;

		SysUser user = (SysUser)request.getSession().getAttribute("user");
		if (null == user){
			/**
			 * signin.jsp :这样是相对路径（在当前路径进行跳转）
			 * /signin.jsp ：告诉它在顶层（webapp）下进行跳转
			 */
			String path = "/signin.jsp";
			response.sendRedirect(path);
			return;
		}
		RequestHolder.add(user);
		RequestHolder.add(request);
		filterChain.doFilter(servletRequest,servletResponse);
		return;
	}

	public void destroy() {

	}
}
