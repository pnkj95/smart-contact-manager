package com.smart.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.helpers.Message;
import com.smart.model.User;
import com.smart.service.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotController {
	
	Random random = new Random(1000);
	
	@Autowired
	private EmailService emailService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
//	handler to open email id form
	@GetMapping("/forgot")
	public String openEmailForm(Model model) {
		model.addAttribute("title","Registered Email!");
		return "forgot_email_form";
	}

	
	@PostMapping("/send-otp")
	public String sendOtp(@RequestParam("email") String email, HttpSession session) {

		//generate otp of 4 digit
		int otp = random.nextInt(9999);
		System.out.println("otp : "+otp);
		
		try {
			//code to send otp to email
			String subject = "SCM - OTP";
			String message = "<h1> OTP = "+ otp + "</h1>";
			String to = email;
			boolean flag = emailService.sendEmail(message, subject, to);
			if(flag) {
				session.setAttribute("my_otp", otp);
				session.setAttribute("email", email);
				session.setAttribute("message", new Message("OTP is sent successfully !! Check your email box","alert-success"));
				return "verify_otp";
			}
			else {
				session.setAttribute("message", new Message("Please check your email !!","alert-danger"));
				return "forgot_email_form";
			}
		} catch (Exception e) {
			session.setAttribute("message", new Message("Please check your email!! ","alert-danger"));
			return "forgot_email_form";
		}
	}
	
	//verify otp
	@PostMapping("/verify-otp")
	public String verifyOTP(@RequestParam("otp") Integer otp, HttpSession session) {
		Integer my_otp = (Integer) session.getAttribute("my_otp");
		String email = (String) session.getAttribute("email");
		System.out.println("myotp : "+my_otp);
		System.out.println("otp : "+otp);
		if(my_otp.equals(otp)) {
			
			User user  = this.userRepository.getUserByUsername(email);
			if(user == null) {
				//send error message
				session.setAttribute("message", new Message("User doesn't exist with this email !! ","alert-danger"));
				return "forgot_email_form";
			}else {
				//send change password form
			}
			//password change form
			return "password_change_form";
		}else {
			session.setAttribute("message", new Message("You have entered wrong OTP! ","alert-danger"));
			return "verify_otp";
		}
	}
	
	//change password
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newPassword") String newPassword, HttpSession session) {
		
		String email = (String) session.getAttribute("email");
		User user = this.userRepository.getUserByUsername(email);
		user.setPassword(bCryptPasswordEncoder.encode(newPassword));
		this.userRepository.save(user);
		//session.setAttribute("message", new Message("Your password has been changed successfully! ","alert-success"));
		return "redirect:/sign-in?change=Your Password has been changed successfully";
	}
}
