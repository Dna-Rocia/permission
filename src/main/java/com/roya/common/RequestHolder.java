package com.roya.common;

import com.roya.model.SysUser;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by idea
 * description :
 *		请求信息放到ThreadLocal（进程）中，减少信息的传输
 * @author Loyail
 * @since 1.8JDK
 * CreateDate 2018-05-14-17:06
 */
public class RequestHolder {
	//相当于Map 当前的进程
	private static final ThreadLocal<SysUser> userHolder = new ThreadLocal<SysUser>();

	private static final ThreadLocal<HttpServletRequest> requestHolder = new ThreadLocal<HttpServletRequest>();

	public static void add(SysUser sysUser) {
		userHolder.set(sysUser);
	}

	public static void add(HttpServletRequest request) {
		requestHolder.set(request);
	}

	public static SysUser getCurrentUser() {
		return userHolder.get();
	}

	public static HttpServletRequest getCurrentRequest() {
		return requestHolder.get();
	}

	//进程结束后要remove掉，否则容易会造成内存泄露
	public static void remove() {
		userHolder.remove();
		requestHolder.remove();
	}





}
