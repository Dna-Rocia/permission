package com.roya.config;

import com.roya.utils.DesUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.Properties;

/**
 * Created by idea
 * description :
 *		JDBC 的加载配置文件
 *	作为自定义类加到配置文件中，解密使用
 * @author Loyail
 * @since 1.8JDK
 * CreateDate 2018-05-19-9:12
 */
public class EncryptablePropertyPlaceholderConfig extends PropertyPlaceholderConfigurer {

	public static final String KEY = DesUtil.PASSWORD_CRYPT_KEY;

	protected void processProperties(ConfigurableListableBeanFactory beanFactory, Properties props)
			throws BeansException {
		try {
			String username = props.getProperty(ConfigConstant.JDBC_DATASOURCE_USERNAME_KEY);
			if (username != null) {
				props.setProperty(ConfigConstant.JDBC_DATASOURCE_USERNAME_KEY, DesUtil.Decrypt(username,KEY ));
			}

			String password = props.getProperty(ConfigConstant.JDBC_DATASOURCE_PASSWORD_KEY);
			if (password != null) {
				props.setProperty(ConfigConstant.JDBC_DATASOURCE_PASSWORD_KEY, DesUtil.Decrypt(password, KEY));
			}

			String url = props.getProperty(ConfigConstant.JDBC_DATASOURCE_URL_KEY);
			if (url != null) {
				props.setProperty(ConfigConstant.JDBC_DATASOURCE_URL_KEY, DesUtil.Decrypt(url,  KEY));
			}

			String driverClassName = props.getProperty(ConfigConstant.JDBC_DATASOURCE_DRIVERCLASSNAME_KEY);
			if(driverClassName != null){
				props.setProperty(ConfigConstant.JDBC_DATASOURCE_DRIVERCLASSNAME_KEY, DesUtil.Decrypt(driverClassName, KEY));
			}

			super.processProperties(beanFactory, props);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BeanInitializationException(e.getMessage());
		}


	}
}