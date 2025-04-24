package com.contact.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.contact.dao.UserRepository;
import com.contact.entities.User1;
import com.contact.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController{ //Home Controller for github push comments
	
	@Autowired
	private BCryptPasswordEncoder passEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping("/")
	public String home() {
		return "home";
	}
	
	@GetMapping("/about")
	public String about() {
		return "about";
	}
	
	@GetMapping("/signup")
	public String signup(Model m) {
		m.addAttribute("title", "Register - Smart Contact Manager");
		m.addAttribute("user", new User1());
		return "signup";
	}
	
	@PostMapping("/do_register")
	public String registerPage(@Valid @ModelAttribute("user") User1 user, BindingResult result1, @RequestParam(value="agreement", defaultValue = "false") boolean agreement, Model model, HttpSession session){
		
		try {
			if(!agreement) {
				System.out.println("you have not agreed the terms and conditions");
				throw new Exception("you have not agreed the terms and conditions");
			}
			
			if(result1.hasErrors()) {
				System.out.println("Error: " + result1);
				model.addAttribute("user", user);
				return "signup";
			}
			
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageurl("default.png");
			user.setPassword(passEncoder.encode(user.getPassword()));
			
			User1 result = this.userRepository.save(user);
			
			model.addAttribute("user", new User1());
			
			session.setAttribute("message", new Message("Successfully Registered!!", "alert-success"));
			return "signup";
		}
		catch(Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went wrong!!" + e.getMessage(), "alert-danger"));
			return "signup";
		}
	}
	
	@PostMapping("/remove-session-message")
    public String removeSessionMessage(HttpSession session) {
        session.removeAttribute("message");
        return "redirect:/signup";
    }
	
	@GetMapping("/signin")
	public String logIn(Model model) {
		model.addAttribute("title", "Login Page");
		return "login";
	}
	
}
