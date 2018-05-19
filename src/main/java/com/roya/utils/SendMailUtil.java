package com.roya.utils;

import com.roya.common.MailSender;
import com.roya.exception.ParamException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

/**
 * Created by idea
 * description :
 *		发送邮件模板工具
 * @author Loyail
 * @since 1.8JDK
 * CreateDate 2018-05-19-15:56
 */
public class SendMailUtil {


	public static MailSender createSender(){
		ApplicationContext ac = new ClassPathXmlApplicationContext("spring-mail.xml");

		MailSender sender = (MailSender) ac.getBean("MailSenderDemo");
		sender.setMailSender((JavaMailSender)ac.getBean("mailSender"));

		String username = ConfigProperUtil.getEncryPropertyByKey("mail.properties","mail.username");
		sender.setSenderAddress(username);

		return sender;
	}


	/**
	 * 信息模板（简单）
	 * @param receiverAddr 收件人邮箱地址
	 * @param subject 邮件主题（标题）
	 * @param text 邮件正文（内容）
	 * @param files 附件（image，excel，word ...etc）
	 */
	public static boolean MimeMessageMail(String receiverAddr,String subject,String text, File... files){
		boolean flag = true;

		MailSender sender =	createSender();
		JavaMailSender javaMailSender = sender.getMailSender();
		MimeMessage mime = javaMailSender.createMimeMessage();
		MimeMessageHelper helper;
		try {
			helper = new MimeMessageHelper(mime, true, "utf-8");
			helper.setTo(receiverAddr);
			helper.setFrom(sender.getSenderAddress());
			helper.setSubject(subject);
			helper.setText(text);
			if (null != files && files.length > 0){
				for (File file: files) {
					FileSystemResource systemResource = new FileSystemResource(file); //附件
					helper.addAttachment(file.getName(),systemResource);
				}
			}
		} catch (MessagingException me) {
			me.printStackTrace();
		}

		try {
			javaMailSender.send(mime);
		}catch (MailException e){
			e.printStackTrace();
			flag = false;
			throw new ParamException("邮件发送失败");
		}
		return flag;

	}


	/**
	 * 信息模板（简单）
	 * @param receiverAddr 收件人邮箱地址
	 * @param subject 邮件主题（标题）
	 * @param text 邮件正文（内容）
	 */
	public  static  boolean  SimpleMessageMail(String receiverAddr, String subject, String text){
		boolean flag = true;
		MailSender sender =	createSender();
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setTo(receiverAddr);
		mail.setFrom(sender.getSenderAddress());
		mail.setSubject(subject);
		mail.setText(text);

		try {
			sender.send(mail);
		}catch (MailException e){
			flag = false;
			e.printStackTrace();
			throw new ParamException("邮件发送失败");
		}
		return flag;
	}





}
