package com.roya.utils.FileUtils;

import com.roya.exception.ParamException;
import com.roya.utils.BeanValidator;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Loyaill
 * @description : 上傳文件
 * @CreateTime 2018-06-11-17:37
 */
public class UploadUtil {

	/**
	 * 上傳圖片
	 * @param request 请求
	 * @param file 图片源
	 * @return 图片访问路径
	 * @throws Exception
	 * @throws IOException
	 */
	public static String upLoadImg(HttpServletRequest request, MultipartFile file, Integer folderType ) throws ParamException ,IOException {
		//校驗文件是否為空
		BeanValidator.checkFileNull(file);
		String fileType = checkImgTypeAndNum(file);
		return finalDepositUrl(request, file, fileType,folderType);
	}


	/**
	 * 上傳文件
	 * @param request 請求
	 * @param file 文件源
	 * @return 文件訪問路徑
	 * @throws ParamException
	 * @throws IOException
	 */
	public static String upLoadFile(HttpServletRequest request, MultipartFile file , Integer folderType ) throws ParamException,IOException {
		//校驗文件是否為空
		BeanValidator.checkFileNull(file);
		String fileType = checkFileTypeAndNum(file);
		return finalDepositUrl(request, file, fileType,folderType);
	}



	public static void  deleteFileOrPath(String url) throws ParamException{
		PathUtil.deleteFileOrPath(url);
	}



	/**
	 * 文件上傳到服務器本地
	 * @param type  文件存放類型編號
	 * @param file 文件
	 * @return 文件存放的具體地址（物理地址）
	 * @throws Exception
	 */
	public static String  uploadFile(Integer type, MultipartFile file) throws Exception{
		//校驗文件是否為空
		BeanValidator.checkFileNull(file);
		//校驗pdf 文件
		checkExtensionNameAndType(type, file);
		return 	 fileStreamWork(file, getFolderMsg(type).get("finalPath").toString());
	}


	/**
	 * 校驗PDF文件
	 * @param type 文件類型編號
	 * @param file 文件數據
	 * @throws ParamException
	 */
	private static void checkExtensionNameAndType(Integer type, MultipartFile file) throws ParamException {
		String extension = PathUtil.getFileExtension(file.getName());
		if (extension.equals("pdf")){
			if ((2 != type))throw new ParamException("類型不支持");
		}
	}


	/**
	 * 根據前端傳入的文件類型進行文件目錄的創建
	 * @param type  文件保存類型
	 * @return 臨時/最終存放位置信息
	 */
	private static Map getFolderMsg(Integer type)throws ParamException{
		//获取文件夹
		String folder  = FolderConstant.getFolder(type);
		//最終上傳位置
		String finalPath = PathUtil.makeDirPath(PathUtil.imgBasePath(),folder);
		Map map = new HashMap();
		map.put("finalPath",finalPath);
		return map;
		//上传时临时文件保存的目录
		//	String tempPath = PathUtil.makeDirPath(PathUtil.imgTempPath(),folder);
		//	map.put("tempPath",tempPath);
	}



	/**
	 * 數據流的工作流程
	 * @param file 文件數據源
	 * @param finalFile 最終文件存放位置
	 * @return 返回文件存放在服務器的路徑
	 * @throws IOException
	 */
	private static String fileStreamWork(MultipartFile file, String finalFile) throws IOException {
		//獲取上傳文件完整路徑
		String final_path = getFinalPath(file, finalFile);
		//输入流
		InputStream inputStream=null;
		//文件輸出流
		FileOutputStream outputStream = null;
		try {
			//创建一个文件输出流
			outputStream = new FileOutputStream(final_path);
			//创建一个缓冲区
			byte buffer[] = new byte[1024];
			//判断输入流中的数据是否已经读完的标识
			int len = 0;
			//獲取上傳文件的輸入流
			inputStream = file.getInputStream();
			//循环将输入流读入到缓冲区当中，(len=in.read(buffer))>0就表示in里面还有数据
			while((len = inputStream.read(buffer))>0){
				//使用FileOutputStream输出流将缓冲区的数据写入到指定的目录(savePath + "\\" + filename)当中
				outputStream.write(buffer, 0, len);
			}
		}finally {
			//關閉輸入流
			if(inputStream!=null){
				inputStream.close();
			}
			//關閉輸出流
			if (outputStream != null){
				outputStream.close();
			}
			//刪除臨時文件 沒借助臨時文件夾
			//PathUtil.deleteFileOrPath(PathUtil.imgTempPath());
		}
		return final_path;
	}


	/**
	 * 獲取最終路徑
	 * @param file 文件數據
	 * @param finalPath 最終文件保存路徑
	 * @return
	 */
	private static String getFinalPath(MultipartFile file, String finalPath) {

		String fileName = file.getName();
		//得到上传文件的扩展名
		String suffix = PathUtil.getFileExtension(fileName);
		//得到新的文件名
		String newName = String.valueOf(System.currentTimeMillis()) + "." + suffix;
		//文件最終路徑
		finalPath += newName;
		return finalPath;
	}




	/**
	 * 根據上傳解析器解析數據并在請求上做監聽處理  暫時不用
	 * @param factory 將工廠傳入
	 * @return
	 */
	private static ServletFileUpload getServletFileUpload(DiskFileItemFactory factory) {
		//2、创建一个文件上传解析器
		ServletFileUpload upload = new ServletFileUpload(factory);
		//监听文件上传进度
		upload.setProgressListener(new ProgressListener(){
			public void update(long pBytesRead, long pContentLength, int arg2) {
				System.out.println("文件大小为：" + pContentLength + ",当前已处理：" + pBytesRead);
			}
		});
		//解决上传文件名的中文乱码
		upload.setHeaderEncoding("UTF-8");
//		upload.parseRequest()
		return upload;
	}





	/**
	 * Apache文件上传组件处理文件（操作是在臨時文件上進行操作） 暫時不用
	 * @param tempFile 臨時文件
	 * @return 返回在臨時文件上創建的工廠
	 */
	private static DiskFileItemFactory getDiskFileItemFactory(File tempFile) {
		//1、创建一个DiskFileItemFactory工厂
		DiskFileItemFactory factory = new DiskFileItemFactory();
		//设置工厂的缓冲区的大小，当上传的文件大小超过缓冲区的大小时，就会生成一个临时文件存放到指定的临时目录当中。
		factory.setSizeThreshold(1024*100);//设置缓冲区的大小为100KB，如果不指定，那么缓冲区的大小默认是10KB
		//设置上传时生成的临时文件的保存目录
		factory.setRepository(tempFile);
		return factory;
	}



	/**
	 * file 轉 MultipartFile
	 * 啟用此方法 先加下面的依賴
	 * <!-- https://mvnrepository.com/artifact/org.springframework/spring-mock -->
		 <dependency>
			 <groupId>org.springframework</groupId>
			 <artifactId>spring-mock</artifactId>
			 <version>RELEASE</version>
		 </dependency>
	 * @param file 文件
	 * @return  MultipartFile
	 * @throws IOException
	 */
//	private static MultipartFile parseFileItems(File file) throws IOException {
//		FileInputStream inputStream = null;
//		inputStream = new FileInputStream(file);
//		return new MockMultipartFile(file.getName(), inputStream);
//	}


	/**
	 *  圖片類型跟尺寸
	 * @param file 文件源
	 * @return 類型
	 * @throws ParamException
	 */
	private static String checkImgTypeAndNum(MultipartFile file) throws ParamException {
		// 圖片类型限制
		String[] picType = PathUtil.getImgExtensionStr().split(",");
		String fileType = checkFileType(file, picType);
		// 图片大小限制
		Integer imgBitNum = PathUtil.getImgBitNum();
		judgeSize(file, imgBitNum);
		return fileType;
	}

	/**
	 * 文件類型與尺寸
	 * @param file 文件源
	 * @return 類型
	 * @throws ParamException
	 */
	private static String checkFileTypeAndNum(MultipartFile file) throws ParamException {
		// 文件类型限制
		String[] fileExten = PathUtil.getFileExtensionStr().split(",");
		String fileType = checkFileType(file, fileExten);
		// 文件大小限制
		Integer fileBitNum = PathUtil.getFileBitNum();
		judgeSize(file, fileBitNum);
		return fileType;
	}


	/**
	 * 判斷文件/圖片尺寸
	 * @param file
	 * @param fileBitNum
	 * @throws ParamException
	 */
	private static void judgeSize(MultipartFile file, Integer fileBitNum) throws ParamException {
		if (fileBitNum > 0 ){
			if (file.getSize() > fileBitNum * 1024 * 1024) {
				throw new ParamException("圖片尺寸過大");
			}
		}
	}


	/**
	 * 校验文件类型
	 * @param file  文件源
	 * @param fileExten 文件后缀
	 * @return 返回对应后缀
	 * @throws ParamException
	 */
	private static String checkFileType(MultipartFile file, String[] fileExten) throws ParamException {

		String srcFileName= file.getOriginalFilename();

		String fileType= FilenameUtils.getExtension(srcFileName);

		boolean allowed = Arrays.asList(fileExten).contains(fileType);
		if (!allowed) {
			throw new ParamException("擴展名不合法");
		}
		return fileType;
	}


	/**
	 * 根据最终存放的位置，生成对应的访问url
	 * @param request 发起的请求
	 * @param file 文件源
	 * @param fileType 文件后缀
	 * @return  访问url
	 * @throws ParamException
	 * @throws IOException
	 */
	private static String finalDepositUrl(HttpServletRequest request, MultipartFile file, String fileType, Integer folderType) throws ParamException, IOException {
		// 生成实际存储的真实文件名
		String realName = UUID.randomUUID().toString() +"."+ fileType;
		//获取文件夹
		String folder  = FolderConstant.getFolder(folderType);
		String realPath=realPath(request,folder);
		File dest=new File(realPath, realName);
		// 将文件写入指定路径下
		file.transferTo(dest);
		// 返回图片的URL地址
		return ("http://" + request.getServerName() + ":" + request.getServerPort() + "/file/"+ folder+realName);
	}




	/**该方法用于获取文件存放的路径*/
	private static String realPath(HttpServletRequest request, String foldername)throws ParamException{
		// 图片存放的真实路径
		String realPath = "";
		switch (1) {
			case 0: {
				//最終上傳位置
				//realPath = request.getServletContext().getRealPath("/WEB-INF/file") ;
				break;
			}
			case 1: {
				realPath = PathUtil.makeDirPath(PathUtil.imgBasePath(),foldername);
				break;
			}
			default: {
				//返回错误信息
				throw new ParamException("未發現文件路徑");
			}
		}
		return realPath;
	}

}
