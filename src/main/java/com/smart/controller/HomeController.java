//BindingResult class should always be placed after ModelAttribute. Avoid placing it as a last argument
package com.smart.controller;


import java.net.InetSocketAddress;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.helpers.Message;
import com.smart.model.User;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@GetMapping("/")
	public String home(Model model, @RequestHeader Map<String, String> headers) {
		model.addAttribute("title","Home - Smart Contact Manager");
		headers.forEach((key, value) -> {
	        //System.out.println(String.format("Header '%s' = %s", key, value));
	    });
		return "home";
	}
	
	@GetMapping("/about")
	public String about(Model model, @RequestHeader(value = "user-agent") String userAgent) {
		model.addAttribute("title","About - Smart Contact Manager");
		//System.out.println("userAgent is : "+userAgent);
		return "about";
	}
	
	@GetMapping("/signup")
	public String signup(Model model, @RequestHeader HttpHeaders headers) {
		model.addAttribute("title","Register - Smart Contact Manager");
		model.addAttribute("user", new User());
		InetSocketAddress host = headers.getHost();
		//System.out.println("host : "+host);
		return "signup";
	}
	//BindingResult should always be placed after ModelAttribute. Avoid placing it as a last argument
	@PostMapping("/doRegister")
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, @RequestParam(value = "agreement",defaultValue = "false") boolean agreement ,Model model, HttpSession session) {
		try {
			if(!agreement) {
				System.out.println("Please select terms and conditions");
				throw new Exception("Please select terms and conditions");
			}
			if(result.hasErrors()) {
				System.out.println("result errors");
				model.addAttribute("user", user);
				return "signup";
			}
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			System.out.println("user######"+user.toString());
			User userResult =  userRepository.save(user);
			model.addAttribute("user",new User());
			session.setAttribute("message",new Message("Successfully registered!!", "alert-success"));
			return "signup";
			
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println("Exception occured : "+e.getMessage());
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went wrong!! "+e.getMessage(),"alert-danger"));
			return "signup";
		}
		
	}
	
	//handler for custom login
	@GetMapping("/sign-in")
	public String customLogin(Model model) {
		model.addAttribute("title","Login - Smart Contact Manager");
		return "login";
		
	}
}
