package com.developer.ws.service;

//import java.io.IOException;
//import java.util.Map;

//import javax.mail.MessagingException;

import com.developer.ws.io.entity.UserEntity;
import com.developer.ws.shared.dto.UserDto;

//import freemarker.template.TemplateException;

public interface EmailService {
	
	void sendVerificationMail(UserDto user);
	boolean sendPasswordResetRequest(UserEntity userEntity, String Token);
	void sendSimpleMessage(String to, String token);

	//void sendSimpleMessageUsingTemplate(String to, String subject, String... templateModel);

	void sendMessageWithAttachment(String to, String subject, String text, String pathToAttachment);
	

	/*
	 * void sendMessageUsingThymeleafTemplate(String to, String subject, Map<String,
	 * Object> templateModel) throws IOException, MessagingException;
	 * 
	 * void sendMessageUsingFreemarkerTemplate(String to, String subject,
	 * Map<String, Object> templateModel) throws IOException, MessagingException;
	 */
}
