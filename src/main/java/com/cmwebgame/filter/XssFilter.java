package com.cmwebgame.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Strings;

/**
 * 针对XSS攻击的过滤器
 * 
 * @author wilson
 * @since 2012/09/03 19:34:29
 */
public class XssFilter implements Filter {

	/**
	 * 需要排除的页面
	 */
	private String excludedURI;

	private String[] excludedUriArray;

	@Override
	public void init(FilterConfig config) throws ServletException {
		excludedURI = config.getInitParameter("excludedURI");
		if (!Strings.isNullOrEmpty(excludedURI)) {
			excludedUriArray = excludedURI.split(",");
		}
		return;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		boolean isExcludedPage = false;
		
		if(excludedUriArray!=null && excludedUriArray.length!=0){
			for (String page : excludedUriArray) {// 判断是否在过滤url之外
				if (((HttpServletRequest) request).getServletPath().equals(page)) {
					isExcludedPage = true;
					break;
				}
			}
		}else {
			isExcludedPage = false;
		}
		

		if (isExcludedPage) {// 在过滤url之外
			chain.doFilter(request, response);
		} else {// 不在过滤url之外
		// System.out.println("request.getParameterMap()1:" +
		// request.getParameterMap() + " | time:" + System.currentTimeMillis());
			XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper(
					(HttpServletRequest) request);
			// System.out.println("xssRequest.getParameterMap():" +
			// xssRequest.getParameterMap() + " | time:" +
			// System.currentTimeMillis());
			// System.out.println("request.getParameterMap()2:" +
			// request.getParameterMap() + " | time:" +
			// System.currentTimeMillis());
			chain.doFilter(xssRequest, response);
		}

	}

	@Override
	public void destroy() {
	}
}