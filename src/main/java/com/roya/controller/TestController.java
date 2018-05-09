package com.roya.controller;

import com.roya.common.JsonData;
import com.roya.exception.ParamException;
import com.roya.exception.PermissionException;
import com.roya.param.TestVo;
import com.roya.utils.BeanValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/test")
@Slf4j
public class TestController {

	@RequestMapping("/hello.json")
	@ResponseBody
	public JsonData hello() {
		log.info("hello");
		throw  new PermissionException("AAA exception");
		//return JsonData.success("hello, permission");
	}

	@RequestMapping("/validate.json")
	@ResponseBody
	public JsonData validate(TestVo vo) throws ParamException{
		log.info("validate");
//		try {
//			Map<String,String> map = BeanValidator.validateObject(vo);
//			if (MapUtils.isNotEmpty(map)){
//				for (Map.Entry<String, String> entry: map.entrySet()) {
//					log.info("{}->{}",entry.getKey(),entry.getValue());
//				}
//			}
//		}catch (Exception e){
//		}
		BeanValidator.check(vo);
		return JsonData.success("test validate");
	}
}
