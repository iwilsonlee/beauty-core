package com.cmwebgame.filter;

public class XssDecoder {

	/**
	 * 將全形字轉回半形字
	 * 
	 * @param s
	 * @return
	 */
	public static String xssDecode(String s) {
		if (s == null || s.isEmpty()) {
			return s;
		}
		StringBuilder sb = new StringBuilder(s.length() + 16);
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
			case '＞':
				sb.append('>');
				break;
			case '＜':
				sb.append('<');
				break;
			case '‘':
				sb.append('\'');
				break;
			case '“':
				sb.append('\"');
				break;
			case '＆':
				sb.append('&');
				break;
			case '＼':
				sb.append('\\');
				break;
			case '＃':
				sb.append('#');
				break;
			case '（':
				sb.append('(');
				break;
			case '）':
				sb.append(')');
				break;
			case '，':
				sb.append(',');
				break;
			default:
				sb.append(c);
				break;
			}
		}
		return sb.toString();
	}

}
