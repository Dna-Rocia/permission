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
 *		MAIL 配置类
 *	作为自定义类加到配置文件中，解密使用
 * @author Loyail
 * @since 1.8JDK
 * CreateDate 2018-05-19-14:29
 */
public class EncryMailPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

	public static final String KEY = DesUtil.PASSWORD_CRYPT_KEY;

	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactory, Properties props) throws BeansException {
		try {
			String username = props.getProperty(ConfigConstant.MAIL_USERNAME_KEY);
			if (username != null) {
				props.setProperty(ConfigConstant.MAIL_USERNAME_KEY, DesUtil.Decrypt(username,KEY ));
			}

			String password = props.getProperty(ConfigConstant.MAIL_PASSWORD_KEY);
			if (password != null) {
				props.setProperty(ConfigConstant.MAIL_PASSWORD_KEY, DesUtil.Decrypt(password, KEY));
			}

			super.processProperties(beanFactory, props);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BeanInitializationException(e.getMessage());
		}
	}
}
