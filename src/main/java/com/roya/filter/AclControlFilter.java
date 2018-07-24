package com.roya.filter;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.roya.common.ApplicationContextHelper;
import com.roya.common.JsonData;
import com.roya.common.RequestHolder;
import com.roya.model.SysUser;
import com.roya.service.SysCoreService;
import com.roya.utils.JsonMapper;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Loyaill
 * @description : 权限拦截
 * @CreateTime 2018-07-24-11:22
 */
@Slf4j
public class AclControlFilter implements Filter {

	//全局要过滤掉的url
	private static Set<String> exclusionUrlSet = Sets.newConcurrentHashSet();

	private final static String noAuthUrl = "/sys/user/noAuth.page";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		//白名单（无需拦截）
		String exclusionUrls = filterConfig.getInitParameter("exclusionUrls");
		List<String> exclusionUrlList = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(exclusionUrls);
		exclusionUrlSet = Sets.newConcurrentHashSet(exclusionUrlList);	//读取配置文件的指定字段，解析出白名单的url
		exclusionUrlSet.add(noAuthUrl);
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest)servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		//获取当前的访问请求
		String servletPath = request.getServletPath();

		//获取参数
		Map requestMap = request.getParameterMap();

		//若当前的请求在白名单中，就不做过滤处理
		if (exclusionUrlSet.contains(servletPath)){
			filterChain.doFilter(servletRequest,servletResponse);//放开拦截
			return;
		}
		//所有进行权限校验操作，必须进行login过滤
		SysUser sysUser = RequestHolder.getCurrentUser();
		if (sysUser == null){
			//JsonMapper.obj2String  对象转成json的格式
			log.info("someone visit {}, but no login , parameter:{}",servletPath, JsonMapper.obj2String(requestMap));
			noAuth(request,response);
			return;
		}
		//因为filter不是被spring管理的   所以就从applicationContextHelper中取出
		SysCoreService sysCoreService = ApplicationContextHelper.popBean(SysCoreService.class);
		if (!sysCoreService.hasUrlAcl(servletPath)){
			log.info("{} visit {}, but no login , parameter:{}",JsonMapper.obj2String(sysUser),servletPath, JsonMapper.obj2String(requestMap));
			noAuth(request,response);
			return;
		}
		filterChain.doFilter(servletRequest,servletResponse);//放开拦截
		return;

	}


	private  void  noAuth(HttpServletRequest request,HttpServletResponse response) throws IOException{
		String servletPath = request.getServletPath();
		if (servletPath.endsWith(".json")){
			JsonData jsonData = JsonData.fail("没有访问权限，如需要访问，请联系管理员");
			response.setHeader("Content-Type","application/json");
			response.getWriter().print(JsonMapper.obj2String(jsonData));
			return;
		}else {
			clientRedirect(noAuthUrl,response);
			return;
		}
	}

	private void clientRedirect(String url, HttpServletResponse response) throws IOException{
		response.setHeader("Content-Type", "text/html");
		response.getWriter().print("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
				+ "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + "<head>\n" + "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"/>\n"
				+ "<title>跳转中...</title>\n" + "</head>\n" + "<body>\n" + "跳转中，请稍候...\n" + "<script type=\"text/javascript\">//<![CDATA[\n"
				+ "window.location.href='" + url + "?ret='+encodeURIComponent(window.location.href);\n" + "//]]></script>\n" + "</body>\n" + "</html>\n");
	}



	@Override
	public void destroy() {

	}
}
