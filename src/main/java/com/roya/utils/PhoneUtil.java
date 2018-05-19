package com.roya.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by idea
 * description :
 *		电话加密解密
 * @author Loyail
 * @since 1.8JDK
 * CreateDate 2018-05-14-14:44
 */
public class PhoneUtil {

	/**
	 * 通过Map值获取key
	 * @return
	 */
	public static String getLetter(String val) {
		for(Map.Entry<String,String> entry:getMapData().entrySet()){
			if(val.equals(entry.getValue()))
				return String.valueOf(entry.getKey());
		}
		return val;
	}

	/**
	 * 字母 b a c g h k o w q p
	 * 数字 0 1 2 3 4 5 6 7 8 9
	 * @return Map
	 */
	public static Map<String,String> getMapData(){
		Map<String,String> map = new HashMap<String,String>();
		map.put("b","0");
		map.put("a","1");
		map.put("c","2");
		map.put("g","3");
		map.put("h","4");
		map.put("k","5");
		map.put("o","6");
		map.put("w","7");
		map.put("q","8");
		map.put("p","9");
		return map;
	}


	/**
	 * 字符串前增加“~”
	 */
	public static String prefixStr(String val){
		return "~"+val;
	}


	/**
	 * 过滤掉带有"~"的字符
	 * @return
	 */
	public  static String  filterStr (String  phoneStr){
		String[] phoneStrs = phoneStr.split("~");
		String newPhoneStr = "";
		for(int i=0,len=phoneStrs.length;i<len;i++){
			newPhoneStr += phoneStrs[i];
		}
		return newPhoneStr;
	}


	/**
	 * 去除所有数字
	 */
	public static String removeNum(String val){
		return val.replaceAll("\\d+","");
	}



	/**
	 * 随机生成i位数字
	 * i<10
	 */
	public static String radomNum(int i){
		Random random = new Random();
		String val=String.valueOf(random.nextInt());
		return val.substring(0,i).indexOf("-")>-1?radomNum(i):val.substring(0,i);
	}



	/**
	 * 随机插入3个数字
	 */
	public static String insertNum(String val){
		String num=radomNum(3);
		//往指定长度字符串加入随机数字
		for(int i=0;i<num.length();i++){
			Integer inNum= Integer.parseInt(radomNum(1));
			String result=val.substring(0,inNum>val.length()?val.length():inNum)+num.charAt(i)+val.substring(inNum>val.length()?val.length():inNum,val.length());
			val=result;
		}
		return val;
	}



	/**
	 * 数字 0 1 2 3 4 5 6 7 8 9
	 * 字母 b a c g h k o w q p
	 * @param val
	 * @return 数字转字母
	 */
	public static String getPhoneStr(String val) {
		String returnStr = "";
		if (val != null && val.length() > 0) {
			byte[] obj = val.getBytes();
			for (int i = 0; i < obj.length; i++) {
				returnStr += getLetter(String.valueOf(val.charAt(i)));
			}
		}
		return returnStr;
	}



	/**
	 * 字母转数字
	 * 字母 b a c g h k o w q p
	 * 数字 0 1 2 3 4 5 6 7 8 9
	 * @param val
	 * @return
	 */
	public static String getPhoneNum(String val){
		String returnStr = "";
		if (val != null && val.length() > 0) {
			byte[] obj = val.getBytes();
			for (int i = 0; i < obj.length; i++) {
				returnStr += getMapData().get(String.valueOf(val.charAt(i)));
			}
		}
		return returnStr;
	}


	/***
	 * 换位号码中两组数字：第二位和第六位交换，第三位和第五位交换，位数号按从左往右数
	 * 13510642584
	 * 16015342584
	 */
	public static String changeNum(String val){

		if(!UtilTools.isEmpty(val)){
			String str="";
			char [] phone=val.toCharArray();
			for(int i=0;i<phone.length;i++){
				if(i==1||i==2||i==4||i==5){
					if(i==1)
						str+=phone[5];
					if(i==2)
						str+=phone[4];
					if(i==4)
						str+=phone[2];
					if(i==5)
						str+=phone[1];
				}else{
					str+=phone[i];
				}
			}
			return str;
		}
		return val;
	}









	/**
	 * ：加密步骤：
	 * 1、换位号码中两组数字：第二位和第六位交换，第三位和第五位交换
	 * 2、将全部号码转换为对应的字符
	 * 3、任意位置插入三个随机数字
	 * 4、在步骤C之后的字符串前加上”~”
	 * @return
	 */
	public static String encryptPhone(String phoneStr){
		//1.换位号码中两组数字：第二位和第六位交换，第三位和第五位交换
		phoneStr=changeNum(phoneStr);
		//2.将全部号码转换为对应的字符
		phoneStr=getPhoneStr(phoneStr);
		//3.任意位置插入三个随机数字
		phoneStr=insertNum(phoneStr);
		//4.在步骤C之后的字符串前加上”~”
		phoneStr=prefixStr(phoneStr);
		return phoneStr;
	}



	/**
	 * ：解密步骤：
	 * 1、去除字符串前”~”
	 * 2、去除所有数字
	 * 3、将剩余字母全部转换为对应的数字
	 * 4、换位号码中两组数字：第二位和第六位交换，第三位和第五位交换
	 * @return
	 */
	public static String decryptPhone(String phoneStr){
		if(!UtilTools.isNumber(phoneStr)){
			//1.去除字符串前”~”
			phoneStr= filterStr(phoneStr);
			//2.去除所有数字
			phoneStr=removeNum(phoneStr);
			//3.将剩余字母全部转换为对应的数字
			phoneStr=getPhoneNum(phoneStr);
			//4.换位号码中两组数字：第二位和第六位交换，第三位和第五位交换
			phoneStr=changeNum(phoneStr);
		}
		return phoneStr;
	}





/*
	public  static  void  main(String[] strings){
		String str = encryptPhone("15829244280");
		System.out.println(str);
		String newStr = decryptPhone(str);
		System.out.println(newStr);
	}
*/


}
