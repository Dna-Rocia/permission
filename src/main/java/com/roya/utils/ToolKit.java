package com.roya.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by idea
 * description :
 *	 常用转换工具
 * @author Loyaill
 * @since 1.8JDK
 * CreateDate 2018-05-19-10:19
 */
public class ToolKit {

	public static final File logFile = new File(".", "app_sl.log");
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("[yyyy/MM/dd HH:mm:ss]");
//  /**
//   * 8位长的随机码
//   */
//  static final String secret8 = "Iv}7xlJ^";


	/**
	 * logger
	 * @param log 日志内容
	 * @param std 是否向控制台打印
	 */
	public static void logger(String log, boolean std){
		OutputStream out = null;
		String dt = null;

		try{
			dt = dateFormat.format(new Date());

			if(std) System.out.println(dt+log);

			if(logFile.length()>1024*1024*20){
				logFile.delete();
			}
			out = new FileOutputStream(logFile, true);
			out.write(dt.getBytes());
			out.write(log.getBytes());
			out.write('\n');
			out.flush();
		}catch(Exception ex){
		}finally{
			try {
				if(null!=out)out.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * logger
	 * @param log
	 */
	public static void logger(String log){
		logger(log, false);
	}

//  /**
//   * 异或加密
//   * @param src
//   * @return
//   */
//  public static byte[] xor(byte[] src){
//      byte[] res = new byte[src.length];
//
//      for(int i=0; i<src.length; i++){
//          res[i] = (byte) (src[i] ^ secret80[i]);
//      }
//
//      return res;
//  }

	/**
	 * 验证摘要与数据一致性
	 * @param digest 摘要
	 * @param src 数据源
	 * @param offset 偏移量
	 * @param len 数据源长度
	 * @return true:一致,false:不一致
	 */
	public static boolean checkDigest(byte[] digest, byte[] src, int offset, int len){
		boolean ok = false;
		final int secret_size = 8;
		byte[] sign = new byte[secret_size+16];

		try{
			System.arraycopy(digest, 0, sign, 0, secret_size);
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(digest,0,secret_size);
			md5.update(src,offset,len);
			md5.digest(sign,secret_size,sign.length-secret_size);
			ok = ToolKit.bytes2Hex(digest).equals(ToolKit.bytes2Hex(sign));
		}catch(Exception ex){
			ex.printStackTrace();
		}

		return ok;
	}

	/**
	 * 生成包含8位随机码+16位MD5的消息摘要
	 * @param src 数据源
	 * @param offset 偏移量
	 * @param len 长度
	 * @return 如果执行成功则返回8+16位的字节摘要,否则返回null
	 */
	public static byte[] digest(byte[] src, int offset, int len){
		final int secret_size = 8;
		byte[] secret = new byte[secret_size];
		byte[] sign = new byte[secret_size+16];
		new Random().nextBytes(secret);

		try{
			System.arraycopy(secret, 0, sign, 0, secret_size);
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(secret);
			md5.update(src,offset,len);
			md5.digest(sign,secret_size,sign.length-secret_size);
		}catch(Exception ex){
			ex.printStackTrace();
			sign = null;
		}

		return sign;
	}

	/**
	 * hex字符串转byte数组<br/>
	 * 2个hex转为一个byte
	 * @param src
	 * @return
	 */
	public static byte[] hex2Bytes1(String src){
		byte[] res = new byte[src.length()/2];
		char[] chs = src.toCharArray();
		int[] b = new int[2];

		for(int i=0,c=0; i<chs.length; i+=2,c++){
			for(int j=0; j<2; j++){
				if(chs[i+j]>='0' && chs[i+j]<='9'){
					b[j] = (chs[i+j]-'0');
				}else if(chs[i+j]>='A' && chs[i+j]<='F'){
					b[j] = (chs[i+j]-'A'+10);
				}else if(chs[i+j]>='a' && chs[i+j]<='f'){
					b[j] = (chs[i+j]-'a'+10);
				}
			}

			b[0] = (b[0]&0x0f)<<4;
			b[1] = (b[1]&0x0f);
			res[c] = (byte) (b[0] | b[1]);
		}

		return res;
	}

	/**
	 * hex字符串转byte数组<br/>
	 * 2个hex转为一个byte
	 * @param src
	 * @return
	 */
	public static byte[] hex2Bytes(String src){
		byte[] res = new byte[src.length()/2];
		char[] chs = src.toCharArray();
		for(int i=0,c=0; i<chs.length; i+=2,c++){
			res[c] = (byte) (Integer.parseInt(new String(chs,i,2), 16));
		}
		return res;
	}

	/**
	 * byte数组转hex字符串<br/>
	 * 一个byte转为2个hex字符
	 * @param src
	 * @return
	 */
	public static String bytes2Hex(byte[] src){
		char[] res = new char[src.length*2];
		final char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
		for(int i=0,j=0; i<src.length; i++){
			res[j++] = hexDigits[src[i] >>>4 & 0x0f];
			res[j++] = hexDigits[src[i] & 0x0f];
		}

		return new String(res);
	}


	public static byte[] hex2byte(byte[] b) {
		if ((b.length % 2) != 0)
			throw new IllegalArgumentException("长度不是偶数");
		byte[] b2 = new byte[b.length / 2];
		for (int n = 0; n < b.length; n += 2) {
			String item = new String(b, n, 2);
			b2[n / 2] = (byte) Integer.parseInt(item, 16);
		}
		return b2;
	}
	public static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
		}
		return hs.toUpperCase();
	}

	/**
	 * 生成安全签字
	 * @param src
	 * @return
	 */
	public static String signature(String src){
		final String secret8 = "Iv}7xlJ^";
		return sha1Hex(secret8+src);
	}

	/**
	 * 返回16进制sha-1加密后信息
	 * @param btInput
	 * @return
	 */
	public static String sha1Hex(byte[] btInput){
		final char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
		try {
			MessageDigest mdInst = MessageDigest.getInstance("SHA-1");
			mdInst.update(btInput);
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];

			for (int i=0,k=0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}

			return new String(str);
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 返回16进制sha-1加密后信息
	 * @param src
	 * @return
	 */
	public static String sha1Hex(String src){
		return sha1Hex(src.getBytes());
	}
	
	
	
	
//	public static void main(String[] a) {
//		byte[] d = hex2Bytes("0002000200020002");
//		String str ="";
//		for (byte byt: d) {
//			System.out.println(byt);
//			str += byt;
//		}
//		System.out.println(str);
//		String rts  = bytes2Hex("0002000200020002".getBytes());
//		System.out.println(rts);
//	}
	
	
	
	
	
}













