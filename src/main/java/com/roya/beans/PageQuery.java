package com.roya.beans;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;

/**
 * Created by idea
 * description :
 *		请求对应页面的结果
 * @author Loyail
 * @since 1.8JDK
 * CreateDate 2018-05-15-16:59
 */
public class PageQuery {
	@Setter
	@Getter
	@Min(value = 1,message = "当前页码不合法")
	private int pageNo = 1;

	@Setter
	@Getter
	@Min(value = 1,message = "每页展示数量不合法")
	private int pageSize = 10;

	@Setter
	private int offset;

	public int getOffset() {
		return  (pageNo -  1) * pageSize;
	}
}
