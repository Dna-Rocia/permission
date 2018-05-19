package com.roya.param;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Created by idea
 * description :
 *		用户的参数
 * @author Loyaill
 * @since 1.8JDK
 * CreateDate 2018-05-14-11:07
 */
@Setter
@Getter
@ToString
public class UserParam {

	private Integer id;

	@NotBlank(message = "用户名不可以为空")
	@Length(max = 20, min = 1, message = "用户名长度需要在1~20个字符以内")
	private String username;

	@NotBlank(message = "电话不可以为空")
	@Length(max = 13, min = 1, message = "电话长度需要在1~13个字符以内")
	private String telephone;

	@NotBlank(message = "邮箱不可以为空")
	@Length(min = 5, max = 50, message = "邮箱长度需要在5~50字符以内")
	private String mail;

	@NotNull(message = "必须提供用户所在的部门")
	private Integer deptId;

	@NotNull(message = "必须指定用户的状态")
	//1：正常 0：冻结 2：删除
	@Min(value = 0, message = "用户状态不合法")
	@Max(value = 2, message = "用户状态不合法")
	private Integer status;

	@Length(min = 0, max = 200, message = "备注长度需要在200字符以内")
	private String remark = "";







}
