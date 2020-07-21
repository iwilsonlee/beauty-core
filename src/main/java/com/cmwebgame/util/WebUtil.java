/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cmwebgame.util;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.util.HttpURLConnection;

import com.cmwebgame.util.preferences.SystemGlobals;

/**
 *
 * @author Administrator
 */
public class WebUtil {

	//	// 臉書的常數設定
	//	public static final String FB_APP_KEY = "d271c7130871f5172330f95adf4ac59e";
	//	public static final String FB_APP_SECRECT = "82d8a0b249063afed57573ae2688fe65";
	public static String CMWEBCODE = "cmwebgame_wilson_lam_sam";

	/**
	 * 取得displaytag隱藏在request中的page number
	 * @param request
	 * @return 0表示沒有找到，>1即為頁碼
	 */
	public static int getPageNumber(HttpServletRequest request) {
		int page = 0;
		Enumeration paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String name = (String) paramNames.nextElement();
			if (name != null && name.startsWith("d-") && name.endsWith("-p")) {
				String pageValue = request.getParameter(name);
				if (pageValue != null) {
					page = Integer.parseInt(pageValue) - 1;
				}
			}
		}
		return page;
	}

	public static int countTotalPage(long recordCount) {
		if (recordCount % 10 == 0) {
			return (int) (recordCount / 10);
		} else {
			return (int) ((recordCount / 10) + 1);
		}
	}

	public static int countTotalPageForHospital(long recordCount) {
		if (recordCount % 6 == 0) {
			return (int) (recordCount / 6);
		} else {
			return (int) ((recordCount / 6) + 1);
		}
	}

	public static String extractShortContent(String fullContent) {
		return fullContent.substring(0, 60);
	}

	/**
	 * 隨機選一個起點，然後回傳符合數量的List
	 * @return
	 */
	public static List randomList(List fullList, int returnSize) {
		List list = new ArrayList();
		if (fullList.size() > 0 && fullList.size() <= returnSize) {
			return fullList;
		}
		if (fullList.size() > 0) {
			int pos = (int) (Math.random() * (fullList.size()));
			for (int i = 0; i < returnSize; i++) {
				list.add(fullList.get(pos));
				pos++;
				// 指標到底，回到開頭
				if (pos >= fullList.size()) {
					pos = 0;
				}
			}
		}
		return list;
	}

	/**
	 * 根据日期计算出年龄
	 * @param birthDay
	 * @return
	 * @throws Exception
	 */
	public static int getAge(Date birthDay) throws Exception {
		Calendar cal = Calendar.getInstance();

		if (cal.before(birthDay)) {
			throw new IllegalArgumentException("The birthDay is before Now.It's unbelievable!");
		}

		int yearNow = cal.get(Calendar.YEAR);
		int monthNow = cal.get(Calendar.MONTH);
		int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
		cal.setTime(birthDay);

		int yearBirth = cal.get(Calendar.YEAR);
		int monthBirth = cal.get(Calendar.MONTH);
		int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

		int age = yearNow - yearBirth;

		if (monthNow <= monthBirth) {
			if (monthNow == monthBirth) {
				//monthNow==monthBirth
				if (dayOfMonthNow < dayOfMonthBirth) {
					age--;
				} else {
					//do nothing
				}
			} else {
				//monthNow>monthBirth
				age--;
			}
		} else {
			//monthNow<monthBirth
			//donothing
		}

		return age;
	}

	/**
	 * 自动生成8位数密码
	 * @return
	 */
	public static String generatePassword() {
		return generatePassword(8);
	}

	/**
	 * 自动生成指定位数的密码
	 * @param length
	 * @return
	 */
	public static String generatePassword(int length) {
		String pstr = "1qaz2hn7ujPLKJHm8ik9ol0pQWERTYUIOwsx3edc4rfv5tgb6yGFDSAZXCVBNM";
		StringBuffer pass = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int idx = (int) (Math.random() * 61) + 1;
			pass.append(pstr.charAt(idx));
		}
		return pass.toString();
	}
	/**
	 * nowtime與temptime的絕對值是否大於abs，是則true，否則false
	 * @param nowtime
	 * @param temptime
	 * @param abs
	 * @return
	 */
	public static boolean checkTime(long nowtime,long temptime, long abs){
    	long timeAbs = Math.abs(nowtime-temptime);
		if( timeAbs > abs){
			return true;
		}
		else
		{
			return false;
		}
    }

	public static boolean checkTime(long nowtime,long temptime, int step){
    	long timeAbs = Math.abs(nowtime-temptime);
		if( timeAbs > step){
			return false;
		}
		else
		{
			return true;
		}
    }
}
