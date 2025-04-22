package com.contact.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.contact.dao.ContactRepository;
import com.contact.dao.UserRepository;
import com.contact.entities.Contact;
import com.contact.entities.User1;
import com.contact.helper.Message;

import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	ContactRepository contactRepository;
	
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) { //we can find unique parameter of User using Principal...
		String userName = principal.getName();
		
		User1 user = userRepository.getUserByUserName(userName);
		
		model.addAttribute("user", user);
	}
	
	@GetMapping("/dashboard")
	public String userDashboard(Model model, Principal principal) { //we can find unique parameter of User using Principal...
		
		return "normalUser/dashboard";
	}
	
	@GetMapping("/add-contact")
	public String addContact(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		
		return "normalUser/add_contact";
	}
	
	//Processing add-contact form...
	@PostMapping("/process-contact")
	public String processContact(
	    @ModelAttribute Contact contact,
	    Principal principal,
	    HttpSession session) {

	    try {
	        String userName = principal.getName();
	        User1 user = userRepository.getUserByUserName(userName);

	        // Default image
	        String imageFileName = "default.png";
	        contact.setImage(imageFileName);
	        contact.setUser(user);
	        user.getContacts().add(contact);

	        userRepository.save(user);

	        session.setAttribute("message", new Message("Contact added successfully!", "success"));
	    } catch (Exception e) {
	        System.out.println("Error saving contact: " + e.getMessage());
	        e.printStackTrace();
	        session.setAttribute("message", new Message("Something went wrong: " + e.getMessage(), "danger"));
	    }

	    return "normalUser/add_contact";
	}

	

	
	//showing all contacts...
	@RequestMapping("show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model m, Principal p) {
		String userName = p.getName();
		User1 user = this.userRepository.getUserByUserName(userName);
		
		Pageable pageable = PageRequest.of(page, 5);
		
		Page<Contact> contactsByUser = this.contactRepository.findContactsByUser(user.getId(), pageable);
		m.addAttribute("contacts", contactsByUser);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", contactsByUser.getTotalPages());
		
		return "normalUser/show_contacts";
	}
	
	// Showing particular Contact...
	@RequestMapping("/contact/{id}")
	public String showParticularContacts(@PathVariable("id") Integer id, Model m, Principal principal) {
		System.out.println("CID is: " + id);

		Optional<Contact> contactOptional = this.contactRepository.findById(id);

		if (contactOptional.isPresent()) {
			Contact contact1 = contactOptional.get();

			String nameOfUser = principal.getName();
			User1 userByUserName = this.userRepository.getUserByUserName(nameOfUser);

			if (userByUserName.getId() == contact1.getUser().getId()) {
				m.addAttribute("model", contact1);
			}

		}
		return "normalUser/contact_detail";
	}
	
	//deleting a contact...
	@GetMapping("/delete/{id}")
	public String deleteContact(@PathVariable("id") Integer id, Model m, Principal principal, HttpSession session) {
		Optional<Contact> contactOptional = this.contactRepository.findById(id);

		if (contactOptional.isPresent()) {
			Contact contact1 = contactOptional.get();

			String nameOfUser = principal.getName();
			User1 userByUserName = this.userRepository.getUserByUserName(nameOfUser);

			if (userByUserName.getId() == contact1.getUser().getId()) {
				userByUserName.getContacts().remove(contact1);
				this.userRepository.save(userByUserName);
//				contact1.setUser(null);
				//this.contactRepository.delete(contact1);
			}
			
			session.setAttribute("message", new Message("Contact Successfully deleted..", "success"));

		}
		
		return "redirect:/user/show-contacts/0";
	}
	
	
	//updating a form...
	@PostMapping("/update-contact/{id}")
	public String updateForm(@PathVariable("id") Integer id, Model m) {
		m.addAttribute("title", "Update Contact");
		
		Contact contact = this.contactRepository.findById(id).get();
		m.addAttribute("contact", contact);
		
		
		return "normalUser/update_form";
	}
	
	@PostMapping("/process-update")
	public String updateHandler(@ModelAttribute Contact contact,@RequestParam("profileImage") MultipartFile file,Model model, Principal principal, HttpSession session  ) {
		try {
			//old contact details...
			Contact oldContactDetails = this.contactRepository.findById(contact.getId()).get();
			//image...
			if(!file.isEmpty()) {
				//file work...
				//rewrite
				
				//delete old photo
				File deleteOldFile = new ClassPathResource("static/image").getFile();
	            File file1 = new File(deleteOldFile, oldContactDetails.getImage());
	            file1.delete();
	            
				//update new photo
				File saveFile = new ClassPathResource("static/image").getFile();
	            Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
	            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
	            contact.setImage(file.getOriginalFilename());
			}
			else {
				contact.setImage(oldContactDetails.getImage());
			}
			User1 user = this.userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);
			
			this.contactRepository.save(contact);
			
			session.setAttribute("message", new Message("Your contact is updated", "success"));
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return "redirect:/user/contact/"+contact.getId();
	}
	
	//Showing Your Profile...
	@GetMapping("/profile")
	public String yourProfile(Model m) {
		m.addAttribute("title", "Profile Page");
		return "normalUser/profile";
	}
}
