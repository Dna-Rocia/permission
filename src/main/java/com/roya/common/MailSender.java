package com.roya.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import javax.annotation.Resource;

/**
 * Created by idea
 * description :
 *		邮件模板
 * @author Loyaill
 * @since 1.8JDK
 * CreateDate 2018-05-18-13:36
 */
@Setter@Getter
public class MailSender {

		@Resource
		private JavaMailSender mailSender;

		private String senderAddress;

		public void send(SimpleMailMessage mail)throws MailException {
			mailSender.send(mail);
		}

		public JavaMailSender getMailSender(){
			return this.mailSender;
		}

		public void setMailSender(JavaMailSender mailSender) {
			this.mailSender = mailSender;
		}


}
