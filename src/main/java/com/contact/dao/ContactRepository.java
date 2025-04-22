package com.contact.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.contact.entities.Contact;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
	
	@Query("SELECT c FROM Contact c WHERE c.user.id = :userId")
	//current page - Page
	//Contact per page
	Page<Contact> findContactsByUser(@Param("userId") int userId, Pageable pePageable);
}
