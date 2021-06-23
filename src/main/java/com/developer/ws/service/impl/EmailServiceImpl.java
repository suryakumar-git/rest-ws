package com.developer.ws.service.impl;

import java.io.File;
//import java.io.IOException;
//import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
//import org.springframework.core.io.Resource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
//import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
//import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

//import com.developer.ws.io.entity.PasswordResetTokenEntity;
import com.developer.ws.io.entity.UserEntity;
import com.developer.ws.service.EmailService;
//import org.thymeleaf.context.Context;
//import org.thymeleaf.spring5.SpringTemplateEngine;
import com.developer.ws.shared.dto.UserDto;

//import freemarker.template.Template;
//import freemarker.template.TemplateException;

/**
 * Created by Olga on 7/15/2016.
 */
@Service
public class EmailServiceImpl implements EmailService {

	private static final String NOREPLY_ADDRESS = "noreply@skrest.com";

	// The subject line for the email.
	final String SUBJECT = "One last step to complete your registration with PracticeApp";

	final String PASSWORD_RESET_SUBJECT = "Password reset request";

	// The HTML body for the email.
	final String HTMLBODY = "<h1>Please verify your email address</h1>"
			+ "<p>Thank you for registering with our mobile app. To complete registration process and be able to log in,"
			+ " click on the following link: "
			+ "<a href='http://localhost:8080/verification-service/email-verification.html?token=$tokenValue'>"
			+ "Final step to complete your registration" + "</a><br/><br/>"
			+ "Thank you! And we are waiting for you inside!";

	// The email body for recipients with non-HTML email clients.
	final String TEXTBODY = "Please verify your email address. "
			+ "Thank you for registering with our mobile app. To complete registration process and be able to log in,"
			+ " open then the following URL in your browser window: "
			+ " http://localhost:8080/verification-service/email-verification.html?token=$tokenValue"
			+ " Thank you! And we are waiting for you inside!";

	final String PASSWORD_RESET_HTMLBODY = "<h1>A request to reset your password</h1>" + "<p>Hi, $firstName!</p> "
			+ "<p>Someone has requested to reset your password with our project. If it were not you, please ignore it."
			+ " otherwise please click on the link below to set a new password: "
			+ "<a href='http://localhost:8080/verification-service/password-reset.html?token=$tokenValue'>"
			+ " Click this link to Reset Password" + "</a><br/><br/>" + "Thank you!";

	// The email body for recipients with non-HTML email clients.
	final String PASSWORD_RESET_TEXTBODY = "A request to reset your password " + "Hi, $firstName! "
			+ "Someone has requested to reset your password with our project. If it were not you, please ignore it."
			+ " otherwise please open the link below in your browser window to set a new password:"
			+ " http://localhost:8080/verification-service/password-reset.html?token=$tokenValue" + " Thank you!";

	@Autowired
	private JavaMailSender emailSender;

	/*
	 * @Autowired private SimpleMailMessage template;
	 */

	/*
	 * @Autowired private SpringTemplateEngine thymeleafTemplateEngine;
	 * 
	 * @Autowired private FreeMarkerConfigurer freemarkerConfigurer;
	 * 
	 * @Value("classpath:/mail-logo.png") private Resource resourceFile;
	 */

	public void sendSimpleMessage(String to, String token) {
		try {
			String htmlBodyWithToken = HTMLBODY.replace("$tokenValue", token);
			// String textBodyWithToken = TEXTBODY.replace("$tokenValue", token);

			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(NOREPLY_ADDRESS);
			message.setTo(to);
			message.setSubject(SUBJECT);
			// message.setText(text);

			try {
				sendHtmlMessage(to, SUBJECT, htmlBodyWithToken);
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			emailSender.send(message);
		} catch (MailException exception) {
			exception.printStackTrace();
		}
	}

	/*
	 * @Override public void sendSimpleMessageUsingTemplate(String to, String
	 * subject, String... templateModel) { String text =
	 * String.format(template.getText(), templateModel); sendSimpleMessage(to,
	 * subject, text); }
	 */

	@Override
	public void sendMessageWithAttachment(String to, String subject, String text, String pathToAttachment) {
		try {
			MimeMessage message = emailSender.createMimeMessage();
			// pass 'true' to the constructor to create a multipart message
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			helper.setFrom(NOREPLY_ADDRESS);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(text);

			FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
			helper.addAttachment("Invoice", file);

			emailSender.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	/*
	 * @Override public void sendMessageUsingThymeleafTemplate(String to, String
	 * subject, Map<String, Object> templateModel) throws MessagingException {
	 * 
	 * Context thymeleafContext = new Context();
	 * thymeleafContext.setVariables(templateModel);
	 * 
	 * String htmlBody = thymeleafTemplateEngine.process("template-thymeleaf.html",
	 * thymeleafContext);
	 * 
	 * sendHtmlMessage(to, subject, htmlBody); }
	 * 
	 * @Override public void sendMessageUsingFreemarkerTemplate(String to, String
	 * subject, Map<String, Object> templateModel) throws IOException,
	 * TemplateException, MessagingException {
	 * 
	 * Template freemarkerTemplate =
	 * freemarkerConfigurer.getConfiguration().getTemplate("template-freemarker.ftl"
	 * ); String htmlBody =
	 * FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerTemplate,
	 * templateModel);
	 * 
	 * sendHtmlMessage(to, subject, htmlBody); }
	 */

	private void sendHtmlMessage(String to, String subject, String htmlBody) throws MessagingException {

		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
		helper.setFrom(NOREPLY_ADDRESS);
		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(htmlBody, true);
		// helper.addInline("attachment.png", resourceFile);
		emailSender.send(message);
	}

	@Override
	public void sendVerificationMail(UserDto user) {

		String htmlBodyWithToken = HTMLBODY.replace("$tokenValue", user.getEmailVerificationToken());

		try {
			sendHtmlMessage(user.getEmail(), SUBJECT, htmlBodyWithToken);
		} catch (MessagingException e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean sendPasswordResetRequest(UserEntity userEntity, String Token) {
		
		String htmlBodyWithToken = PASSWORD_RESET_HTMLBODY.replace("$tokenValue",  Token);
		htmlBodyWithToken = htmlBodyWithToken.replace("$firstName",  userEntity.getFirstName());
		
		try {
			sendHtmlMessage(userEntity.getEmail(), PASSWORD_RESET_SUBJECT, htmlBodyWithToken);
			return true;
		} catch (MessagingException e) {
			e.printStackTrace();
			return false;
		}
				
	}


}