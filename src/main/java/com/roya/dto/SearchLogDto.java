package com.roya.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author Loyaill
 * @description :
 * @CreateTime 2018-07-25-15:57
 */
@Getter
@Setter
public class SearchLogDto {

	private Integer type;

	private String  beforeSeg;

	private String  afterSeg;

	private String operator;

	private Date fromTime;

	private Date toTime;

}
