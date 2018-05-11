package com.roya.common;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by idea
 * description :
 *
 * @author Loyaill
 * @version 2.0.0
 * CreateDate 2018-05-09-11:24
 * @since 1.8JDK
 */
@Setter
@Getter
public class JsonData {

	private boolean ret;
	private  String msg;
	private  Object data;

	public JsonData(boolean ret){
		this.ret = ret;
	}

	public  static  JsonData success(){
		return new JsonData(true);
	}


	public  static  JsonData success(Object object){
		JsonData jsonData = new JsonData(true);
		jsonData.data = object;
		return jsonData;
	}

	public  static  JsonData success(Object object,String msg){
		JsonData jsonData = new JsonData(true);
		jsonData.data = object;
		jsonData.msg = msg;
		return jsonData;
	}


	public  static  JsonData fail(String msg){
		JsonData jsonData = new JsonData(false);
		jsonData.msg = msg;
		return jsonData;
	}

	public Map<String,Object> objectMap() {
		HashMap<String,Object> result = new HashMap<String, Object>();
		result.put("ret",ret);
		result.put("msg",msg);
		result.put("data",data);
		return result;
	}
}
