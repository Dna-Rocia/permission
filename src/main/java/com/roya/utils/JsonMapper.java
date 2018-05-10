package com.roya.utils;

import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;
import org.codehaus.jackson.type.TypeReference;

/**
 * Created by idea
 * description :
 *		数据的解析转换
 * @author Loyaill
 * @version 2.0.0
 * CreateDate 2018-05-09-17:04
 * @since 1.8JDK
 */
@Slf4j
public class JsonMapper {
	//json进行序列化与反序列化使用的辅助类
	private static ObjectMapper objectMapper = new ObjectMapper();

	//config
	static {
		/*启用指定DeserializationConfig功能的方法*/
		objectMapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
		/*用于改变该对象映射器的开/关序列化特征的状态的方法。*/
		objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,false);
		objectMapper.setFilters(new SimpleFilterProvider().setFailOnUnknownId(false));
		/*一种设计POJO属性包含策略的系列化方法。*/
		objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);
	}

	/**
	 * 将对象转换成字符串
	 * @param src 传入对象的参数值
	 * @param <T> 传入对象类型
	 * @return 字符串
	 */
	public  static <T> String obj2String(T src){
		if (src == null){
			return null;
		}
		try{
			return src instanceof String ? (String) src : objectMapper.writeValueAsString(src);
		}catch (Exception e){
			log.warn("parse object to string exception,error:{}",e);
			return null;
		}
	}


	/**
	 * 将字符串转换成对象
	 * @param src 传入的字符串
	 * @param tTypeReference
	 * @param <T> 泛型参数作用域
	 * @return 任何对象
	 */
	public  static <T> T string2Object(String src, TypeReference<T> tTypeReference){
		if (src == null  || tTypeReference == null){
			return null;
		}

		try{
			return (T) (tTypeReference.getType().equals(String.class)?src:objectMapper.reader(tTypeReference));
		}catch (Exception e){
			log.warn("parse String to Object exception , String:{},TypeReference<T>:{},error:{}",src,tTypeReference.getType(),e);
			return null;
		}

	}

}
