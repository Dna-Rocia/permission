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
 *
 * @author Loyail
 * @since 1.8JDK
 * CreateDate 2018-05-22-13:53
 */
@Setter
@Getter
@ToString
public class AclParam {

	private Integer id;

	@NotBlank(message = "权限名称不可以为空")
	@Length(min = 2,max = 20,message = "权限名称字符长度在2~20之间")
	private String name;

	@NotNull(message = "必须指定权限模块")
	private Integer aclModuleId;

	@Length(min = 6,max = 100,message = "权限URL字符长度在6~100之间")
	private String url;

	@NotNull(message = "必须指定权限类型")
	@Max(value = 3,message = "权限类型不合法")
	@Min(value = 1,message = "权限类型不合法")
	private Integer type;

	@NotNull(message = "必须指定权限状态")
	@Max(value = 1,message = "权限状态不合法")
	@Min(value = 0,message = "权限状态不合法")
	private Integer status;

	@NotNull(message = "必须指定权限的展示顺序")
	private Integer seq;

	@Length(min = 0,max = 200,message = "权限备注字符长度在200以内")
	private String remark;
}
