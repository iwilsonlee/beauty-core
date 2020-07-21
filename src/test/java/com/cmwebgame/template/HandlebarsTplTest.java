package com.cmwebgame.template;

import java.io.IOException;
import java.util.HashMap;

import net.sf.json.JSON;
import net.sf.json.JSONArray;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.google.common.collect.Maps;

public class HandlebarsTplTest {

  @Before
  public void setUp() throws Exception {

  }

  @After
  public void tearDown() throws Exception {
  }
  
//  @Test
  public void testTpl(){
    Handlebars handlebars = new Handlebars();

    Template template;
	try {
		template = handlebars.compileInline("Hello {{this}}!");
		System.out.println(template.apply("Handlebars.java"));
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    
  }
  
  @Test
  public void testTemplateName(){
	  String template = "aaaa/dfsfs.html";
	  String themeName = template.substring(0, template.indexOf("/"));
	  template = template.substring(template.indexOf("/")+1,template.length());
	  System.out.println("themeName:"+themeName);
	  System.out.println("template:"+template);
  }
//  @Test
  public void testFileTpl(){
	  TemplateLoader loader = new ClassPathTemplateLoader();
	  loader.setPrefix("/templates");
	  loader.setSuffix(".html");
	  Handlebars handlebars = new Handlebars(loader);
	  
	  HashMap<String, Object> data = Maps.newHashMap();
	  data.put("header", "lyh");
	  data.put("title", "hello world");
	  data.put("father", "<b>lwc</b>");
	  data.put("footer", "jmx");


	try {
		Template template = handlebars.compile("hbstest");
		
		System.out.println(template.apply(data));
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	  
  }

}
