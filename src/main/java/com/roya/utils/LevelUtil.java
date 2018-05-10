package com.roya.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by idea
 * description :
 *		部门层级的工具
 * @author Loyail
 * @version 2.0.0
 * CreateDate 2018-05-10-13:30
 * @since 1.8JDK
 */
public class LevelUtil {

	public final  static  String SEPARATOR = ".";

	public final static String ROOT = "0";

	/**
	 * 计算部门的层级
	 */
	public  static String calculateLevel(String parentLevel, Integer parentId){
		if (StringUtils.isBlank(parentLevel)) { //是首层 0
			return  ROOT;
		}else {
			return StringUtils.join(parentLevel,SEPARATOR,parentId);
		}
	}




}
