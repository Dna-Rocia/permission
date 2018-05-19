package com.roya.utils;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created by idea
 * description :
 *		从配置文件中获取值
 * @author Loyail
 * @since 1.8JDK
 * CreateDate 2018-05-19-14:44
 */
public class ConfigProperUtil {

	private static Properties props = new Properties();

	/**
	 * 解析properties文件。
	 * @param  path：properties文件的路径
	 * @param  key： 获取对应key的属性（无需解密）
	 * @return String：返回对应key的属性，失败时候为null。
	 */
	public static String getPropertyByKey(String path,String key){
		String result = null;

		try {
			InputStream inputStream = ConfigProperUtil.class.getClassLoader().getResourceAsStream(path);
			if(inputStream != null){
				props.load(inputStream);
				result = props.getProperty(key,null).trim();
				inputStream.close();
			}else {
				System.out.println("请检查文件路径/key值是否正确");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("根据key值getValue,Value解密失败");
		}
		return result;
	}

	/**
	 * 解析properties文件
	 * @param  path：properties文件的路径
	 * @param  key： 获取对应key的属性（需解密）
	 * @return String：返回对应key的属性，失败时候为null。
	 */
	public static String getEncryPropertyByKey(String path,String key){
		String value = null;
		try {
			InputStream inputStream = ConfigProperUtil.class.getClassLoader().getResourceAsStream(path);
			if(inputStream != null){
				props.load(inputStream);
				value = props.getProperty(key,null).trim();

				value = DesUtil.Decrypt(value);

				inputStream.close();
			}else {
				System.out.println("请检查文件路径/key值是否正确");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("根据key值getValue,Value解密失败");
		}
		return value;
	}




//public static void main(String[] a){
//	System.out.println(getPropertyByKey("mail.properties","mail.username"));
//}

}
