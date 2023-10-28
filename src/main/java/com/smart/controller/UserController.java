package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.helpers.Message;
import com.smart.model.Contact;
import com.smart.model.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;


@Controller
@RequestMapping("/user")
public class UserController {

	public static int CONTACTS_PER_PAGE = 2;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContactRepository contactRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	//method for adding common data to response
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String username = principal.getName();
		System.out.println("username : "+username);
		
		//get the user using username from repository
		User user = userRepository.getUserByUsername(username);
		//System.out.println(user.toString());
		model.addAttribute("user", user);
	}
	
	//dashboard
	@GetMapping("/index")
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
		
	}
	
	//open add form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add-contact";
	}
	
	//handler to process add contact form
	@PostMapping("/process-contact")
	public String processContact(@Valid @ModelAttribute Contact contact,BindingResult result, Model model, Principal principal, @RequestParam("profileImage") MultipartFile file, HttpSession session) {
		
		try {
			String name = principal.getName();
			User user = this.userRepository.getUserByUsername(name);
			user.getContacts().add(contact);
			contact.setUser(user);
			
			if(result.hasErrors()) {
				System.out.println("result errors***********");
				model.addAttribute("contact", contact);
				return "normal/add-contact";
			}
			
			//processing and uploading file
			if(!file.isEmpty()) {
				contact.setImageUrl(file.getOriginalFilename());
				File f = new ClassPathResource("static/img").getFile();
				Path target = Paths.get(f.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Your image has been uploaded to "+target);
			}else {
				System.out.println("File is empty. Default one will be uploaded");
				//throw new Exception("Please select a file to upload");
				contact.setImageUrl("contacts.png");
//				File f = new ClassPathResource("static/img").getFile();
//		        Path sourceDirectory = Paths.get("/img"+File.separator+contact.getImageUrl());
//
//				
//				Path target = Paths.get(f.getAbsolutePath() + File.separator + contact.getImageUrl());
//				Files.copy(sourceDirectory, target, StandardCopyOption.REPLACE_EXISTING);
//				System.out.println("Your default image has been uploaded to "+target);
			}
			this.userRepository.save(user);
			System.out.println("Your contact has been saved to the management system");
			
			model.addAttribute("contact",new Contact());
			session.setAttribute("message",new Message("Your contact is added successfully!!", "alert-success"));
			return "normal/add-contact";
			
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println("Exception occured : "+e.getMessage());
			model.addAttribute("contact", contact);
			session.setAttribute("message", new Message("*Something went wrong!! "+e.getMessage(),"alert-danger"));
			return "normal/add-contact";
		}
		
	}
	
	//handler to show contacts
	@GetMapping("/display/{currentPage}")
	public String fetchContacts(@PathVariable("currentPage") Integer currentPage, Model model, Principal principal) {
		
		/* fetch contacts by using User Repo */
//		String username = principal.getName();
//		User user = this.repository.getUserByUsername(username);
//		List<Contact> contact = user.getContacts();
		String username = principal.getName();
		int userId = this.userRepository.getUserByUsername(username).getId();
		
		Pageable pageable = PageRequest.of(currentPage, CONTACTS_PER_PAGE);
		Page<Contact> contacts = this.contactRepository.findContactsByUser(userId, pageable);
		model.addAttribute("title", "Display User Contacts");
		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("totalPages", contacts.getTotalPages());
		
		return "normal/display-contacts";
	}
	
	//handler to show the contact details
	@GetMapping("/{cId}/contact")
	public String showContactDetails(@PathVariable("cId") Integer id, Model model, Principal principal) {
		
		Contact contact = this.contactRepository.findById(id).get();
		String username = principal.getName();
		User user = userRepository.getUserByUsername(username);
		
		if(user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact" , contact);
			model.addAttribute("title",contact.getFirstName());
		}else {
			model.addAttribute("title","Insufficient Permission");
		}
		
		
		return "normal/contact-details";
	}
	
	@GetMapping("/delete/{cId}")
	public String deleteContact(@PathVariable("cId") Integer cid, Model model, Principal principal, HttpSession session) {
		try {
		//Contact contact = contactRepository.findById(cid).get();
		Optional<Contact> optional = contactRepository.findById(cid);
		Contact contact = optional.get();
		
		User user = userRepository.getUserByUsername(principal.getName());
		if (user.getId() == contact.getUser().getId()) {
			contactRepository.delete(contact);
			File f = new ClassPathResource("static/img").getFile();
			Path path = Paths.get(f.getAbsolutePath() + File.separator + contact.getImageUrl());
	
			if (Files.exists(path) && !contact.getImageUrl().isEmpty() && !contact.getImageUrl().equals("contacts.png")) {
				Files.delete(path);
			}
			System.out.println("Your contact & associated image has been deleted successfully ");
			session.setAttribute("message", new Message("Contact deleted successfully","alert-success"));
		}else {
			model.addAttribute("title","Insufficient Permission");
		}
		return "redirect:/user/display/0";
		}catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Message("Something went wrong! "+e.getMessage(),"danger"));
			return "redirect:/user/display/0";
		}
	}
	
	//using PostMapping not to get accessed this api easily
	@PostMapping("edit-contact/{cId}")
	public String openUpdateForm(@PathVariable("cId") Integer id, Model model) {
		
		model.addAttribute("title","Edit Contact");
		Contact contact = contactRepository.findById(id).get();
		model.addAttribute("contact", contact);
		return "normal/edit-contact";
	}
	
	@PostMapping("/update-contact")
	public String updateContact(@ModelAttribute Contact contact, Model model, @RequestParam("profileImage") MultipartFile file, Principal principal, HttpSession session) {
		
		try {
			System.out.println("contact id : "+contact.getId());
			Contact oldContactDetails = contactRepository.findById(contact.getId()).get();
			if(!file.isEmpty()) {
				//delete old file
				File deleteFile = new ClassPathResource("static/img").getFile();
				File file1 = new File(deleteFile, oldContactDetails.getImageUrl());
				file1.delete();
				
				//update new image
				File f = new ClassPathResource("static/img").getFile();
				Path target = Paths.get(f.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
				contact.setImageUrl(file.getOriginalFilename());
			}else {
				contact.setImageUrl(oldContactDetails.getImageUrl());
			}
			User user = userRepository.getUserByUsername(principal.getName());
			contact.setUser(user);
			contactRepository.save(contact);
			session.setAttribute("message", new Message("Your contact is updated successfully","alert-success"));
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Message("Something went wrong!"+e.getMessage(),"alert-danger"));
		}
		return "redirect:/user/"+contact.getId() + "/contact";
	}
	
	@GetMapping("/my-profile")
	public String profile(Model model) {
		model.addAttribute("title", "User Profile!");
		return "normal/profile";
	} 
	
	@GetMapping("/contacts/{name}")
	@ResponseBody
	public List<Contact> getUsersByName(@PathVariable("name") String prefix, Principal principal, Model model,HttpSession session){
		String username = principal.getName();
		User user = userRepository.getUserByUsername(username);
		
		List<Contact> contacts = contactRepository.getSearchData(prefix, user.getId());
		if(contacts.isEmpty()) {
			System.out.println("No data found!");
			System.out.println("going to same page");
		}
		else {
			System.out.println("Data found of size : "+contacts.size());
			
			System.out.println("going to fresh page");
		}
		return contacts;
	}
	
	
	//handler to open settings page
	@GetMapping("/settings")
	public String openSettings(Model model, HttpServletRequest request) {
		model.addAttribute("title", "Settings");
		System.out.println("The context path is : "+request.getContextPath());
		return "normal/settings";
	}
	
	//handler to change password
		@PostMapping("/change-password")
		public String changePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword, Principal principal, HttpSession session) {
			String username = principal.getName();
			User currentUser = userRepository.getUserByUsername(username);
			String dbPass = currentUser.getPassword();
			if(bCryptPasswordEncoder.matches(oldPassword, dbPass)) {
				currentUser.setPassword(bCryptPasswordEncoder.encode(newPassword));
				this.userRepository.save(currentUser);
				session.setAttribute("message", new Message("Your Password has been changed successfully","alert-success"));
			}else {
				session.setAttribute("message", new Message("Please enter your password correctly!!","alert-danger"));
				return "redirect:/user/settings";
			}
			
			return "redirect:/user/settings";
		}
}
