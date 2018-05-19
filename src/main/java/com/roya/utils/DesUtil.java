package com.roya.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * Created by idea
 * description :
 * AES 算法 对称加密，密码学中的高级加密标准
 *
 * @author Loyail
 * @since 1.8JDK
 * CreateDate 2018-05-19-9:16
 */
public class DesUtil {

	private final static String SECRET_KEY = "AES";
	private final static String CIPHER = "AES/CBC/PKCS5Padding"; //"算法/模式/补码方式"
	private final static byte[] VI = "0102030405060708".getBytes();

	private static boolean checkKeyNull(String sKey) {
		if (sKey == null) {
			System.out.print("Key为空null");
			return true;
		}
		// 判断Key是否为16位
		if (sKey.length() != 16) {
			System.out.print("Key长度不是16位");
			return true;
		}
		return false;
	}

	private static String getOriginalString(Cipher cipher, byte[] encrypted1) {
		try {
			byte[] original = cipher.doFinal(encrypted1);
			String originalString = new String(original);
			return originalString;
		} catch (Exception e) {
			System.out.println(e.toString());
			return null;
		}
	}

	// 加密
	public static String Encrypt(String sSrc, byte[] sKey) throws Exception {
		byte[] raw = sKey;
		//KeyGenerator 生成aes算法密钥
		SecretKeySpec skeySpec = new SecretKeySpec(raw, SECRET_KEY);
		Cipher cipher = Cipher.getInstance(CIPHER);//"算法/模式/补码方式"  //使用加密模式初始化 密钥
		IvParameterSpec iv = new IvParameterSpec(VI);//使用CBC模式，需要一个向量iv，可增加加密算法的强度
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
		byte[] encrypted = cipher.doFinal(sSrc.getBytes());

		return Base64.encodeBase64String(encrypted);//此处使用BAES64做转码功能，同时能起到2次加密的作用。
	}

	// 解密
	public static String Decrypt(String sSrc, byte[] sKey) throws Exception {
		try {
			byte[] raw = sKey;
			SecretKeySpec skeySpec = new SecretKeySpec(raw, SECRET_KEY);
			Cipher cipher = Cipher.getInstance(CIPHER);
			IvParameterSpec iv = new IvParameterSpec(VI);
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			byte[] encrypted1 = Base64.decodeBase64(sSrc);//先用bAES64解密
			return getOriginalString(cipher, encrypted1);
		} catch (Exception ex) {
			System.out.println("解密失败 ：" + ex.toString());
			return null;
		}
	}

	// 加密
	public static String Encrypt(String sSrc, String sKey) throws Exception {
		if (checkKeyNull(sKey)) return null;
		byte[] raw = sKey.getBytes();
		SecretKeySpec skeySpec = new SecretKeySpec(raw, SECRET_KEY);
		Cipher cipher = Cipher.getInstance(CIPHER);//"算法/模式/补码方式"
		IvParameterSpec iv = new IvParameterSpec(VI);//使用CBC模式，需要一个向量iv，可增加加密算法的强度
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
		byte[] encrypted = cipher.doFinal(sSrc.getBytes());

		return Base64.encodeBase64String(encrypted);//此处使用BAES64做转码功能，同时能起到2次加密的作用。
	}


	// 解密
	public static String Decrypt(String sSrc, String sKey) throws Exception {
		try {
			// 判断Key是否正确
			if (checkKeyNull(sKey)) return null;

			byte[] raw = sKey.getBytes("ASCII");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, SECRET_KEY);
			Cipher cipher = Cipher.getInstance(CIPHER);
			IvParameterSpec iv = new IvParameterSpec(VI);
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			byte[] encrypted1 = Base64.decodeBase64(sSrc);//先用bAES64解密
			return getOriginalString(cipher, encrypted1);
		} catch (Exception ex) {
			System.out.println("解密失败 ：" + ex.toString());
			return null;
		}
	}


//	public static void main(String[] args) {
//		try {
//			System.out.println("密文 ：" + Encrypt("root", ToolKit.hex2Bytes(EncryptablePropertyPlaceholderConfig.JDBC_DESC_KEY)));
//
//			//System.out.println("明文 ："+ Decrypt("x6qTIDQ83NkWpHuR90YwPQ==","0002000200020002".getBytes()));
//
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}


}
