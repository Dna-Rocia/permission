package com.roya.beans;

import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Created by idea
 * description :
 *		得到结果（多条）分页
 * @author Loyail
 * @since 1.8JDK
 * CreateDate 2018-05-15-16:59
 */
@Getter
@Setter
@ToString
@Builder
public class PageResult <T>{
	private List<T> data = Lists.newArrayList();

	private int total = 0;

}
