package com.smart.model;


import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern.Flag;

@Entity
@Table(name = "CONTACT")
public class Contact {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	@NotNull
	@NotBlank(message = "*Please enter the first name")
	@Column(name = "first_name")
	private String firstName;
	
	@NotNull
	@NotBlank(message = "*Please enter the second name")
	@Column(name = "second_name")
	private String secondName;
	
	@NotNull
	@NotBlank(message = "*Please enter your work details")
	private String work;
	
	@NotNull
	@NotBlank(message = "*Please enter the phone number")
	@Size(min = 10, max = 10, message = "*Please enter the phone number of 10 digits only")
	private String phone;
	
	@NotNull
	@NotEmpty(message = "*Please enter your email field")
	@Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}",flags = Flag.CASE_INSENSITIVE, message = "Please enter email in correct format")
	@Column(unique = true)
	private String email;
	
	@Column(length = 5000)
	private String description;
	
//	@NotNull
//	@NotBlank(message = "*Please upload an image to proceed further")
	private String imageUrl;
	
	@JsonBackReference
	@ManyToOne
	private User user;
	
	public Contact() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Contact(int id, String firstName, String secondName, String work, String phone, String email, String description,
			String imageUrl) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.secondName = secondName;
		this.work = work;
		this.phone = phone;
		this.email = email;
		this.description = description;
		this.imageUrl = imageUrl;
	}

	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getSecondName() {
		return secondName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "Contact [id=" + id + ", firstName=" + firstName + ", secondName=" + secondName + ", work=" + work
				+ ", phone=" + phone + ", email=" + email + ", description=" + description + ", imageUrl=" + imageUrl
				+ ", user=" + user + "]";
	}

	
	
	
	
}
