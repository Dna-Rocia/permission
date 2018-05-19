package com.roya.utils;



public class UtilTools {

	/**
	 * 正则表达式数字验证
	 * @author Long.Tang
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str) {
		if (str != null && !str.equals("")) {
			java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("[0-9]*");
			java.util.regex.Matcher match = pattern.matcher(str);
			return match.matches();
		} else {
			return false;
		}
	}


	/**
	 * 字符串非空非null判断 long.tang
	 */
	public static boolean isEmpty(String val) {
		if (val == null || val.equals("") || val.equalsIgnoreCase("null")) {
			return true;
		} else {
			return false;
		}
	}











}
