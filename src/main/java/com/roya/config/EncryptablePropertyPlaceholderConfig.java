package com.roya.config;

import com.roya.utils.DesUtil;
import com.roya.utils.ToolKit;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.Properties;

/**
 * Created by idea
 * description :
 *
 * @author Loyail
 * @since 1.8JDK
 * CreateDate 2018-05-19-9:12
 */
public class EncryptablePropertyPlaceholderConfig extends PropertyPlaceholderConfigurer {


	/******************************JDBC相关BEGIN***************************************/
	public static final String JDBC_DESC_KEY = ToolKit.bytes2Hex("0002000200020002".getBytes());

	/**数据库类型**/
	public static final String JDBC_DATASOURCE_DRIVERCLASSNAME_KEY = "db.driverClassName";

	public static final String JDBC_DATASOURCE_URL_KEY = "db.url";

	public static final String JDBC_DATASOURCE_USERNAME_KEY = "db.username";

	public static final String JDBC_DATASOURCE_PASSWORD_KEY = "db.password";

	/******************************JDBC相关END***************************************/




	protected void processProperties(ConfigurableListableBeanFactory beanFactory, Properties props)
			throws BeansException {
		try {
			String username = props.getProperty(JDBC_DATASOURCE_USERNAME_KEY);
			if (username != null) {
				props.setProperty(JDBC_DATASOURCE_USERNAME_KEY, DesUtil.Decrypt(username, ToolKit.hex2Bytes(JDBC_DESC_KEY)));
			}

			String password = props.getProperty(JDBC_DATASOURCE_PASSWORD_KEY);
			if (password != null) {
				props.setProperty(JDBC_DATASOURCE_PASSWORD_KEY, DesUtil.Decrypt(password, ToolKit.hex2Bytes(JDBC_DESC_KEY)));
			}

			String url = props.getProperty(JDBC_DATASOURCE_URL_KEY);
			if (url != null) {
				props.setProperty(JDBC_DATASOURCE_URL_KEY, DesUtil.Decrypt(url,  ToolKit.hex2Bytes(JDBC_DESC_KEY)));
			}

			String driverClassName = props.getProperty(JDBC_DATASOURCE_DRIVERCLASSNAME_KEY);
			if(driverClassName != null){
				props.setProperty(JDBC_DATASOURCE_DRIVERCLASSNAME_KEY, DesUtil.Decrypt(driverClassName, ToolKit.hex2Bytes(JDBC_DESC_KEY)));
			}

			super.processProperties(beanFactory, props);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BeanInitializationException(e.getMessage());
		}


	}
}