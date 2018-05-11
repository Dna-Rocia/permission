package com.roya.param;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;


/**
 * Created by idea
 * description :
 *
 * @author Loyail
 * @version 2.0.0
 * CreateDate 2018-05-10-11:51
 * @since 1.8JDK
 */
@Setter
@Getter
@ToString
public class DeptParam {

	private Integer id;

	@NotBlank(message = "部门名称不能为空")
	@Length(max = 15,min = 2,message = "部门名称字符长度需要在2~15之间")
	private String name;

	private Integer parentId = 0;

	@NotNull(message = "展示顺序不可以为空")
	private Integer seq;

	@Length(max = 150,message = "备注长度需要在150个字符以内")
	private String remark;


}
