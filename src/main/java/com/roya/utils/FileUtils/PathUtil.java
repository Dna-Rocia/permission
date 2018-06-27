package com.roya.utils.FileUtils;

import com.roya.exception.ParamException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Loyaill
 * @description :路径的处理
 * @CreateTime 2018-06-11-15:13
 */
public class PathUtil {

	public static String seperator = File.separator;


	private static String imgExtensionStr = "bmp,gif,jpeg,png,jpg";

	private static String fileExtensionStr = "doc,docx,pdf";

	private static Integer imgBitNum = 3;

	private static Integer fileBitNum = 0;


	public static Integer getImgBitNum() {
		return imgBitNum;
	}

	public static Integer getFileBitNum() {
		return fileBitNum;
	}

	public static String getImgExtensionStr() {
		return imgExtensionStr;
	}

	public static String getFileExtensionStr() {
		return fileExtensionStr;
	}

// 最好使用配置的方式 處理變量
//	public static void setFileBitNum(Integer fileBitNum) {
//		if (null == fileBitNum) fileBitNum = 0;
//		PathUtil.fileBitNum = fileBitNum;
//	}

// 最好使用配置的方式 處理變量
//	public static void setImgExtensionStr() {
//		PathUtil.imgExtensionStr += ","+ imgExtensionStr;
//	}

// 最好使用配置的方式 處理變量
//	public static void setFileExtensionStr() {
//		PathUtil.fileExtensionStr += ","+fileExtensionStr;
//	}

	private static Map commonPath(){
		String path = System.getProperty("user.dir");
		path = path.replace("\\", seperator);
		String pro_name = path.substring(path.lastIndexOf("\\")+1);
		path= path.substring(0,path.lastIndexOf(seperator));
		Map map = new HashMap();
		map.put("pro_name",pro_name);
		map.put("path",path);
		return map;
	}

	/**
	 * 最終保存位置   + map.get("pro_name") +seperator
	 * @return
	 */
	public static String imgBasePath() {
		Map map = commonPath();
		return map.get("path") +seperator+ "file" +seperator;
	}

	/**
	 * 臨時保存位置  + map.get("pro_name") +seperator
	 * @return
	 */
	public static String imgTempPath() {
		Map map = commonPath();
		return map.get("path") +seperator+ "temp" +seperator;
	}


	/**
	 * 拿到文件的扩展名
	 * @return 扩展名
	 */
	public static String getFileExtension(String fileName) {
		return fileName.substring(fileName.lastIndexOf(".")+1);
	}



	/**
	 * 创建目标路径所涉及到的目录
	 * @param targetAddr 目標文件位置
	 * @param position 根路徑位置
	 */
	public static String makeDirPath(String position , String targetAddr) {
		String realPPath = position+targetAddr;

		File dFile = new File(realPPath);

		if (!dFile.exists()) {

			dFile.mkdirs();

		}
		return realPPath;
	}



	/**
	 * 删除文件或文件夹下的所有文件
	 * @param position  若为文件就删文件，反之，是目录就直接删除目录下所有文件
	 */
	public static void  deleteFileOrPath(String position) throws ParamException {
		extractString(position,position.indexOf("/")-1);
		File fileOrPath = new File(position);
		deleteOperation(fileOrPath);
	}


	private static String extractString(String s,int pos){
		for(int i = 0; i < pos; i++){
			s = s.substring(s.indexOf("/")+1 );
		}
		return s;
	}


	/**
	 * 刪除文件夾或是文件
	 * @param dirFile 需要刪除的具體路徑
	 * @return 刪除結果
	 */
	private static boolean deleteOperation(File dirFile) throws ParamException {
		// 如果dir对应的文件不存在，则退出
		if (!dirFile.exists()) {
			throw  new ParamException("資源已被刪除");
		}
		if (dirFile.isFile()) {
			return dirFile.delete();
		} else {
			for (File file : dirFile.listFiles()) {
				deleteOperation(file);
			}
		}
		return dirFile.delete();
	}



}
