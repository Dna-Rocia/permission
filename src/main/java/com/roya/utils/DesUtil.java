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
	/** 加密算法,可用 DES,DESede,Blowfish. */
	private final static String SECRET_KEY = "AES";

	//"算法/模式/补码方式"
	private final static String CIPHER = "AES/CBC/PKCS5Padding";

	private final static byte[] VI = "0102030405060708".getBytes();

	/** 加密、解密key. */
	public static final String PASSWORD_CRYPT_KEY = "0001000200030004";



	//===============================================案例一:二次加密===========================================================

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
		//KeyGenerator 生成aes算法密钥
		SecretKeySpec skeySpec = new SecretKeySpec(sKey, SECRET_KEY);
		//"算法/模式/补码方式"  //使用加密模式初始化 密钥
		Cipher cipher = Cipher.getInstance(CIPHER);
		// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
		IvParameterSpec iv = new IvParameterSpec(VI);
		// 用密匙初始化Cipher对象
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
		byte[] encrypted = cipher.doFinal(sSrc.getBytes());
		//此处使用BAES64做转码功能，同时能起到2次加密的作用。
		return Base64.encodeBase64String(encrypted);
	}


	// 解密
	public static String Decrypt(String sSrc, byte[] sKey) throws Exception {
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(sKey, SECRET_KEY);
			Cipher cipher = Cipher.getInstance(CIPHER);
			IvParameterSpec iv = new IvParameterSpec(VI);
			// 用密匙初始化Cipher对象
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			//先用bAES64解密
			byte[] encrypted1 = Base64.decodeBase64(sSrc);
			return getOriginalString(cipher, encrypted1);
		} catch (Exception ex) {
			System.out.println("解密失败 ：" + ex.toString());
			return null;
		}
	}


	/**
	 * 对数据进行DES加密，再次BAES64转码处理
	 * @param sSrc 待进行DES加密的数据
	 * @return 返回经过DES加密后的数据
	 * @throws Exception
	 */
	public static String Encrypt(String sSrc) throws Exception {
		return Encrypt(sSrc, PASSWORD_CRYPT_KEY.getBytes());
	}

	/**
	 * 对用DES加密过的数据 先BAES64解密
	 * @param sSrc DES加密数据
	 *  @param sKey DES加密的key
	 * @return 返回解密后的数据
	 * @throws Exception
	 */
	public static String Decrypt(String sSrc, String sKey) throws Exception {
		// 判断Key是否正确
		if (checkKeyNull(sKey)) return null;
		return Decrypt(sSrc,sKey.getBytes());
	}

	public static String Decrypt(String sSrc) throws Exception {
		return Decrypt(sSrc,PASSWORD_CRYPT_KEY.getBytes());
	}





	//===============================================案例二：只进行一次===========================================================



	/**
	 * 加密.
	 */
	private static byte[] encrypt(byte[] data, byte[] key) throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(key, SECRET_KEY);
		Cipher cipher = Cipher.getInstance(CIPHER);//"算法/模式/补码方式"
		IvParameterSpec iv = new IvParameterSpec(VI);//使用CBC模式，需要一个向量iv，可增加加密算法的强度
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
		return cipher.doFinal(data);
	}
	/**
	 * 解密.
	 */
	private static byte[] decrypt(byte[] data, byte[] key) throws Exception {
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(key, SECRET_KEY);
			Cipher cipher = Cipher.getInstance(CIPHER);
			IvParameterSpec iv = new IvParameterSpec(VI);
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			return cipher.doFinal(data);
		} catch (Exception ex) {
			System.out.println("解密失败 ：" + ex.toString());
			return null;
		}
	}


	/**
	 * 对用DES加密过的数据进行解密.
	 * @param data DES加密数据
	 *  @param key DES加密的key
	 * @return 返回解密后的数据
	 * @throws Exception
	 */
	public final static String decrypt(String data,String key) throws Exception {
		return new String(decrypt(ToolKit.hex2byte(data.getBytes()),key.getBytes()));
	}

	/**
	 * 对数据进行DES加密.
	 * @param data 待进行DES加密的数据
	 * @return 返回经过DES加密后的数据
	 * @throws Exception
	 */
	public final static String encrypt(String data) throws Exception  {
		return ToolKit.byte2hex(encrypt(data.getBytes(), PASSWORD_CRYPT_KEY.getBytes()));
	}



//	public static void main(String[] args) {
//		try {
//			System.out.println("密文 ：" + Encrypt(""));
//
//			System.out.println("明文 ："+ Decrypt("",PASSWORD_CRYPT_KEY));
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}


}
