package com.roya.param;

import lombok.Getter;
import lombok.Setter;


/**
 * @author Loyaill
 * @description :
 * @CreateTime 2018-07-25-15:54
 */

@Getter
@Setter
public class SearchLogParam {

	private Integer type;

	private String  beforeSeg;

	private String  afterSeg;

	private String operator;

	private String fromTime;

	private String toTime;


}
