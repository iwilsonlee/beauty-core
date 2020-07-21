package com.cmwebgame.template;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.exception.BeetlException;
import org.beetl.ext.web.SessionWrapper;
import org.beetl.ext.web.WebVariable;

/**
 *  通常web渲染的类，将request变量赋值给模板，同时赋值的还有session,request,ctxPath
 *  
 * @author Wilson
 *
 */
public class BwebRender {

	GroupTemplate gt = null;

	public BwebRender(GroupTemplate gt)
	{
		this.gt = gt;
	}

	/**
	 * @param key 模板资源id
	 * @param request
	 * @param response
	 * @param args 其他参数，将会传给modifyTemplate方法
	 */
	public void render(String key, HttpServletRequest request, HttpServletResponse response, ServletContext context, Object... args)
	{
		Writer writer = null;
		OutputStream os = null;
		try

		{
			//			response.setContentType(contentType);
			Template template = gt.getTemplate(key);
			
			Enumeration<String> attrs = request.getAttributeNames();
			while (attrs.hasMoreElements())
			{
				String attrName = attrs.nextElement();
				template.binding(attrName, request.getAttribute(attrName));

			}
			
			Enumeration<String> contextAttrs = context.getAttributeNames();
			while (contextAttrs.hasMoreElements())
			{
				String attrName = contextAttrs.nextElement();
				template.binding(attrName, context.getAttribute(attrName));

			}
			
			WebVariable webVariable = new WebVariable();
			webVariable.setRequest(request);
			webVariable.setResponse(response);
			webVariable.setSession(request.getSession());

			template.binding("session", new SessionWrapper(webVariable.getSession()));

			template.binding("servlet", webVariable);
			template.binding("request", request);
			String themeName = key.substring(0, key.indexOf("/"));
			String templatePath = template.cf.getProperty("RESOURCE.root")+"/"+themeName;
			template.binding("templatePath", templatePath);
			template.binding("ctxPath", request.getContextPath());
			template.binding("templateRealPath", request.getContextPath()+templatePath);

			modifyTemplate(template, key, request, response, args);

			if (gt.getConf().isDirectByteOutput())
			{
				os = response.getOutputStream();
				template.renderTo(os);
			}
			else
			{
				writer = response.getWriter();
				template.renderTo(writer);
			}

		}
		catch (IOException e)
		{
			handleClientError(e);
		}
		catch (BeetlException e)
		{
			handleBeetlException(e);
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

	/**
	 * 可以添加更多的绑定
	 * @param template 模板
	 * @param key 模板的资源id
	 * @param request 
	 * @param response
	 * @param args  调用render的时候传的参数
	 */
	protected void modifyTemplate(Template template, String key, HttpServletRequest request,
			HttpServletResponse response, Object... args)
	{

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

	/**处理客户端抛出的IO异常
	 * @param ex
	 */
	protected void handleBeetlException(BeetlException ex)
	{
		throw ex;
	}

}
