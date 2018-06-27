package com.roya.utils.FileUtils;

import com.roya.exception.ParamException;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Loyaill
 * @description : 保存文件常量
 * @CreateTime 2018-06-11-14:21
 */
public enum FolderConstant {

	USER_AVATAR(1, "user_avatar/", "用户头像"),
	PDF_COURSE(2, "pdf_course/", "PDF教程"),
	OTHER_FILE(3, "other_file/", "其他资源");




	/**
	 * 获取文件夹
	 * @param folderType
	 * @return
	 */
	public static String getFolder(Integer folderType) throws ParamException {
		String value = null;
		for (FolderConstant folderConstant:  FolderConstant.values()) {
			if (folderConstant.type == folderType){
				value = folderConstant.url;
				break;
			}
		}
		if (StringUtils.isBlank(value) ) throw  new ParamException("未找到文件夾所在");
		return value;
	}


	// region 文件夾信息


	private int type;
	private String url;
	private String desc;

	FolderConstant(Integer type, String url, String desc) {
		this.type = type;
		this.url = url;
		this.desc = desc;
	}


	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	//endregion
}
