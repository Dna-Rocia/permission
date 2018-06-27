package com.roya.common;

import com.roya.model.BaseModel;
import com.roya.utils.IpUtil;

import java.util.Date;

/**
 * Created by idea
 * description :
 *		针对与操作的详细
 * @author Loyail
 * @since 1.8JDK
 * CreateDate 2018-05-23-9:33
 */
public class OperateHandle {

	public static void generateSetOperate( BaseModel model) {
		model.setOperator(RequestHolder.getCurrentUser().getUsername());
		model.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
		model.setOperateTime(new Date());
	}

}
