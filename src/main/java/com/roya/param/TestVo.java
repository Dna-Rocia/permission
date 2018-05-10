package com.roya.param;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by idea
 * description :
 *
 * @author Loyail
 * @version 2.0.0
 * CreateDate 2018-05-09-16:15
 * @since 1.8JDK
 */
@Setter
@Getter
public class TestVo {
	@NotBlank
	private String msg;
	@NotNull
	private Integer id;

	private List<String> list;


}
