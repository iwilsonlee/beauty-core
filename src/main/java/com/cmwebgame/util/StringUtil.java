package com.cmwebgame.util;

import java.text.SimpleDateFormat;

/**
 * 字符串處理類
 * @author wilson
 *
 */
public class StringUtil {
	/**
	 * 漢字轉化unicode
	 * @param gbString
	 * @return
	 */
	public static String encodeUnicode(final String gbString) {
		char[] utfBytes = gbString.toCharArray();
		String unicodeBytes = "";
		for (int byteIndex = 0; byteIndex < utfBytes.length; byteIndex++) {
			String hexB = Integer.toHexString(utfBytes[byteIndex]);
			if (hexB.length() <= 2) {
				hexB = "00" + hexB;
			}
			unicodeBytes = unicodeBytes + "\\u" + hexB;
		}
//		System.out.println(unicodeBytes);
		return unicodeBytes;
	}

	/**
	 * unicode转化汉字  
	 * @param dataStr
	 * @return
	 */
	public static StringBuffer decodeUnicode(final String dataStr) {
		final StringBuffer buffer = new StringBuffer();
		String tempStr = "";
		String operStr = dataStr;
//		System.out.println(operStr.indexOf("\\u"));
		if (operStr != null && operStr.indexOf("\\u") == -1)
			return buffer.append(operStr);
		if (operStr != null && !operStr.equals("") && !operStr.startsWith("\\u")) {
			tempStr = operStr.substring(0, operStr.indexOf("\\u"));
			operStr = operStr.substring(operStr.indexOf("\\u"), operStr.length());// operStr字符一定是以unicode编码字符打头的字符串  
		}
		buffer.append(tempStr);
		// 循环处理,处理对象一定是以unicode编码字符打头的字符串  
		while (operStr != null && !operStr.equals("") && operStr.startsWith("\\u")) {
			tempStr = operStr.substring(0, 6);
			operStr = operStr.substring(6, operStr.length());
			String charStr = "";
			charStr = tempStr.substring(2, tempStr.length());
			char letter = (char) Integer.parseInt(charStr, 16); // 16进制 parse整形字符串。  
			buffer.append(new Character(letter).toString());
			if (operStr.indexOf("\\u") == -1) {
				buffer.append(operStr);
			} else { // 处理 operStr使其打头字符为unicode字符  
				tempStr = operStr.substring(0, operStr.indexOf("\\u"));
				operStr = operStr.substring(operStr.indexOf("\\u"), operStr.length());
				buffer.append(tempStr);
			}
		}
		return buffer;
	}
	
	/**
	 * 判斷指定字符串是否符合指定的日期格式，是則ture，否則false
	 * @param rStr
	 * @param rDateFormat
	 * @return
	 */
	public static boolean ValidDateStr(String rStr, String rDateFormat) {
		SimpleDateFormat formatter = new SimpleDateFormat(rDateFormat);
		formatter.setLenient(false);
		try {
			formatter.format(formatter.parse(rStr));
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 判斷指定字符串是否全是數字
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		if(str == null || str.trim().equals("")){
			return false;
		}
		for (int i = str.length(); --i >= 0;) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	public static void main(String[] args){
//		String testString = "this\\u8fb2sg\\u7530ew\\u5df2msg\\u6210wr\\u529f\\u5347\\u7d1a\\u5230\\u7b49ew2221\\u7d1a5";
//		StringBuffer buffer = decodeUnicode(testString);
//		System.out.println(buffer.toString());
		String enString = "咩咩在城池巧虎的建築倉庫升級到1級，巧虎更加繁榮了！";
		encodeUnicode(enString);
		
	}
}
