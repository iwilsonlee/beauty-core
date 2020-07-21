package com.cmwebgame.template;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.exception.BeetlException;
import org.beetl.ext.web.SessionWrapper;
import org.beetl.ext.web.WebVariable;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.google.common.collect.Maps;

/**
 *  通常web渲染的类，将request变量赋值给模板，同时赋值的还有session,request,ctxPath
 *  
 * @author Wilson
 *
 */
public class HandlebarsRender {
	
	private String prefix = "/templates/";
	private String suffix = ".html";
	
	TemplateLoader loader = null;

	public HandlebarsRender(TemplateLoader loader, String themeName)
	{
		
		this.loader = (loader == null) ? new ClassPathTemplateLoader() : loader;
		this.loader.setPrefix(prefix+themeName);
		this.loader.setSuffix(suffix);
	}

	/**
	 * @param key 模板资源id
	 * @param request
	 * @param response
	 * @param args 其他参数，将会传给modifyTemplate方法
	 */
	public void render(String key, HttpServletRequest request, HttpServletResponse response, Object... args)
	{
		Writer writer = null;
		OutputStream os = null;
		try

		{
			Handlebars handlebars = new Handlebars(this.loader);
			Enumeration<String> attrs = request.getAttributeNames();
			HashMap<String, Object> data = Maps.newHashMap();
			while (attrs.hasMoreElements())
			{
				
				String attrName = attrs.nextElement();
				data.put(attrName, request.getAttribute(attrName));

			}
			
			data.put("session", request.getSession());
			data.put("request", request);
			data.put("templatePath", this.loader.getPrefix());
			data.put("ctxPath", request.getContextPath());
			
			com.github.jknack.handlebars.Template templateHbs = handlebars.compile(key);
			
//			System.out.println(templateHbs.apply(data));
			writer = response.getWriter();
			templateHbs.apply(data, writer);

		}
		catch (IOException e)
		{
			handleClientError(e);
		}
		

		finally
		{
			try
			{
				if (writer != null)
					writer.flush();
				if (os != null)
				{
					os.flush();
				}
			}
			catch (IOException e)
			{
				handleClientError(e);
			}
		}
	}

	/**处理客户端抛出的IO异常
	 * @param ex
	 */
	protected void handleClientError(IOException ex)
	{
		//do nothing
		try {
			throw ex;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
