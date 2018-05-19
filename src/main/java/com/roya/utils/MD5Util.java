package com.roya.utils;
import lombok.extern.slf4j.Slf4j;
import java.security.MessageDigest;


/**
 * 密码加密
 */
@Slf4j
public class MD5Util {
	/**
	 *  MD5加密 生成32位md5码
	 */
	//1.密文大写
	public final static String encrypt(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = s.getBytes();
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			log.error("generate md5 error, {}", s, e);
			return null;
		}
	}


	/**
	 *  加密解密算法 执行一次加密，两次解密
	 * @param inStr
	 * @return
	 */
	public static String convertMD5(String inStr) {

		char[] a = inStr.toCharArray();
		for (int i = 0; i < a.length; i++) {
			a[i] = (char) (a[i] ^ 't');
		}
		String s = new String(a);
		return s;

	}


	public static String md5Decode(String str) {
		return convertMD5(convertMD5(str));
	}


//	public static void main(String[] args)throws Exception {
//		String s = new String("123456");
//		System.out.println("encrypt :"+encrypt(s));
//	}





}
