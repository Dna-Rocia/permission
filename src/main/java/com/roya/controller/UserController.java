package com.roya.controller;

import com.roya.model.SysUser;
import com.roya.service.SysUserService;
import com.roya.utils.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by idea
 * description :
 *		普通用户页面 跳转操作
 * @author Loyail
 * @since 1.8JDK
 * CreateDate 2018-05-14-15:18
 */
@Controller
public class UserController {

	@Resource
	private SysUserService userService;


	@RequestMapping("/logout.page")
	public void logout(HttpServletRequest request,HttpServletResponse response) throws IOException {
		request.getSession().invalidate(); //失效
		String path = "signin.jsp";
		response.sendRedirect(path); //当前页面跳转
	}




	//只做页面跳转
	@RequestMapping("/login.page")
	public void login(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");

		SysUser sysUser = userService.findUserByKey(username);
		String errorMsg = "";
		String ret = request.getParameter("ret");

		if (StringUtils.isBlank(username)) {
			errorMsg = "用户名不可以为空";
		} else if (StringUtils.isBlank(password)) {
			errorMsg = "密码不可以为空";
		} else if (sysUser == null) {
			errorMsg = "查询不到指定的用户";
		} else if (!sysUser.getPassword().equals(MD5Util.encrypt(password))) {
			errorMsg = "用户名或密码错误";
		} else if (sysUser.getStatus() != 1) {
			errorMsg = "用户已被冻结，请联系管理员";
		} else {
			// login success
			request.getSession().setAttribute("user", sysUser);
			if (StringUtils.isNotBlank(ret)) {
				response.sendRedirect(ret);
			} else {
				response.sendRedirect("/admin/index.page");
				return;
			}
		}
		//若登录失败，将错误信息写回页面
		request.setAttribute("error", errorMsg);
		request.setAttribute("username", username);
		if (StringUtils.isNotBlank(ret)) {
			request.setAttribute("ret", ret);
		}
		String path = "signin.jsp";
		request.getRequestDispatcher(path).forward(request, response); //当前页面跳转
	}

}
