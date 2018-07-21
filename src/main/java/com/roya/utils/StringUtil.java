package com.roya.utils;

import com.google.common.base.Splitter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Loyaill
 * @description : 字符串转化的所需的结构
 * @CreateTime 2018-07-21-14:33
 */
public class StringUtil {

	public static List<Integer> split2ListInt(String str){
		List<String> strList = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(str);
		return strList.stream().map(strItem -> Integer.parseInt(strItem)).collect(Collectors.toList());
	}



}
