package com.cmwebgame.util;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.cmwebgame.ConfigLoader;
import com.cmwebgame.util.CommonUtils;
import com.cmwebgame.util.preferences.ConfigKeys;
import com.cmwebgame.util.preferences.SystemGlobals;

public class CommonUtilsTest {

	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
	}

	//@Test
	public void testIsNumeric() {
         List<String> list = new ArrayList();
         list.add("1231334324");
         list.add("aaabbbccc");
         list.add("eeedddccc");
         list.add("wwwsssxxx");
         list.add("qqqaaazzz");
         
         if(list.contains("wwwsssxxx11")){
        	 System.out.println("true");
         }else {
        	 System.out.println("false");
		}
	}

	//@Test
	public void testValidDateStr() {
//		fail("Not yet implemented");
		String rStr = "2010/12/30";
		String rDateFormat = "yyyy/MM/dd";
		boolean result = CommonUtils.validDateStr(rStr, rDateFormat);
		assertTrue(result);
	}

	@Test
	public void testLoadClasspath(){
//		URL pathString = this.getClass().getResource("/cmweb-impl.properties");
//		if (new File(SystemGlobals.getValue(pathString.getPath())).exists()) {
//			SystemGlobals.loadAdditionalDefaults(SystemGlobals.getValue(pathString.getPath()));
//		}
	}
}
