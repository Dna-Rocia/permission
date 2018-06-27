package com.roya.utils.FileUtils;

import com.roya.exception.ParamException;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

/**
 * @author Loyaill
 * @description : 下載
 * @CreateTime 2018-06-12-11:44
 */
public class DownloadUtil {

	/**
	 * 文件下載 暫不用
	 * @param file_position  文件的具體存放位置
	 * @return 該文件的二進制byte[]
	 * @throws Exception
	 */
	public static ResponseEntity<byte[]> downloadFile(String file_position) throws Exception{

		File file = new File(file_position);
		if (!file.exists()) throw new ParamException("資源已被刪除");
		String file_name = file_position.substring(file_position.lastIndexOf("\\")+1);

		HttpHeaders headers = new HttpHeaders();

		headers.setContentDispositionFormData("attachment", URLEncoder.encode(file_name,"utf-8"));

		ResponseEntity<byte[]> byteArr = null;

		try{
			byteArr = new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file),headers, HttpStatus.OK);

		}catch (IOException e){
			e.printStackTrace();
		}
		return byteArr;
	}


	/**
	 * 文件的下載
	 * @param file_position 文件存放的位置
	 * @param response 響應
	 * @throws Exception
	 */
	public  static  void  downloadFile(String file_position, HttpServletResponse response) throws Exception {
		//得到要下载的文件
		File file = new File(file_position);
		//如果文件不存在
		if (!file.exists()) throw new ParamException("資源已被刪除");
		//文件名
		String file_name = file_position.substring(file_position.lastIndexOf("\\")+1);
		//设置响应头，控制浏览器下载该文件
		response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(file_name, "UTF-8"));

		FileStreamOperate(file_position, response);
	}


	/**
	 * 下載時候文件流的流程處理
	 */
	private static void FileStreamOperate(String file_position, HttpServletResponse response) throws IOException {
		//读取要下载的文件，保存到文件输入流
		FileInputStream in = new FileInputStream(file_position);
		//创建输出流
		OutputStream out = response.getOutputStream();
		//创建缓冲区
		byte buffer[] = new byte[1024];
		int len = 0;
		//循环将输入流中的内容读取到缓冲区当中
		while((len=in.read(buffer))>0){
			//输出缓冲区的内容到浏览器，实现文件下载
			out.write(buffer, 0, len);
		}
		//关闭文件输入流
		in.close();
		//关闭输出流
		out.close();
	}


}
