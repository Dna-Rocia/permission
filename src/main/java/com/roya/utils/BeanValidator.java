package com.roya.utils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.roya.exception.ParamException;
import org.apache.commons.collections.MapUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;

/**
 * Created by idea
 * description :
 *		请求参数的校验工具类
 * @author Loyaill
 * @version 2.0.0
 * CreateDate 2018-05-09-14:39
 * @since 1.8JDK
 */
public class BeanValidator {
		//定义一个全局的校验工厂
		private  static ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

	/**
	 * 普通的校验方法
	 * @param t 参数值
	 * @param groups
	 * @param <T> 参数类型
	 * @return  map key:错误字段   value:错误信息
	 */
	public static <T> Map<String,String> validate(T t,Class... groups){
		Validator validator  = validatorFactory.getValidator();
		Set validateResult = validator.validate(t,groups);
		if (validateResult.isEmpty()){
			return Collections.emptyMap();
		}else {
			LinkedHashMap errors = Maps.newLinkedHashMap();
			Iterator iterator = validateResult.iterator();
			while (iterator.hasNext()){
				ConstraintViolation violation = (ConstraintViolation)iterator.next();
				errors.put(violation.getPropertyPath(),violation.getMessage());
			}
			return errors;
		}
	}


	public static  Map<String,String> validateList(Collection<?> collection){
		Preconditions.checkNotNull(collection);
		Iterator iterator = collection.iterator();
		Map errors;
		do{
			if (!iterator.hasNext()){
				return Collections.emptyMap();
			}
			Object object = iterator.next();
			errors = validate(object,new Class[0]);
		}while(errors.isEmpty());

		return  errors;
	}



	public static  Map<String,String> validateObject(Object first,Object... objects){
		if (objects != null && objects.length >0){
			return  validateList(Lists.asList(first,objects));
		}else {
			return  validate(first,new Class[0]);
		}
	}


	public static void check(Object param)throws ParamException{
		Map<String,String> map = BeanValidator.validateObject(param);
		if (MapUtils.isNotEmpty(map)){
			throw  new ParamException(map.toString());
		}
	}


	public static void checkFileNull(MultipartFile file) throws ParamException{
		if (null == file && file.isEmpty()) {
			throw  new ParamException();
		}
	}

}
