package com.contact.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.contact.dao.UserRepository;
import com.contact.entities.User1;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		User1 userByUserName = userRepository.getUserByUserName(username);
		if(userByUserName == null) {
			throw new UsernameNotFoundException("could not found user !!");
		}
		
		CustomUserDetails custom = new CustomUserDetails(userByUserName);
		return custom;
	}

}
