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
 * CreateDate 2018-05-21-9:50
 */
@Getter
@Setter
@ToString
public class AclModuleParam {

	private Integer id;

	@NotBlank(message = "权限模块名称不可以为空")
	@Length(max = 20,min = 2,message = "权限模块名称的长度在2~20个字符之间")
	private String name;

	private Integer parentId = 0;

	@NotNull(message = "权限模块展示顺序不可以为空")
	private Integer seq;

	@NotNull(message = "权限模块状态不可以为空")
	@Min(value = 0,message = "权限模块状态不合法")
	@Max(value = 1,message = "权限模块状态不合法")
	private Integer status;

	@Length(max = 150,message = "权限模块的备注长度需在150个字符以内")
	private String remark;








}
