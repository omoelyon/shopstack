package com.shopstack.controller.event;

import java.util.UUID;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.shopstack.entities.user.User;
import com.shopstack.service.user.UserService;

@Component
public class RegistrationListner implements ApplicationListener<OnRegistrationCompleteEvent> {

	
	
	@Autowired
	private MessageSource messages;
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private UserService userServiceImpl;
	
	
	
	@Override
	public void onApplicationEvent(OnRegistrationCompleteEvent event) {
		// TODO Auto-generated method stub
		this.confirmRegistration(event);
		
	}
	
	
	
	private void confirmRegistration(OnRegistrationCompleteEvent event) {
		
		System.out.println(event.getUser().getShopOwner());
		
		//generate token for user
		User user = event.getUser();
		String token = UUID.randomUUID().toString();
		
		//save verification token to the database	 
		userServiceImpl.createVerificationTokenForUser(user, token);
		
		//create confimation url
		String recepientAddress = user.getShopOwner().getEmail();
		String subject = "Registration Confimation";
		String confirmationUrl = event.getAppUrl() + "/shop-owner/registrationConfirm?token="+token;
        String message = messages.getMessage("message.regCon", null, event.getLocale());		
		//send email
		SimpleMailMessage email = new SimpleMailMessage();
		
		email.setTo(recepientAddress);
		email.setSubject(subject);
		email.setText(message +  "http://localhost:8080" + confirmationUrl);
		
		System.out.println(confirmationUrl);
		
		try {
			mailSender.send(email);
			System.out.println("Mail sent successfully");
		}catch(Exception mailException) {
			mailException.printStackTrace();
		}
				
		
	}

}
