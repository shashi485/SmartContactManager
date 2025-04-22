package com.contact.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.contact.entities.User1;

@Repository
public interface UserRepository extends JpaRepository<User1, Integer> {

	@Query("select u from User1 u where u.email = :email")
	public User1 getUserByUserName(@Param("email") String email);
}
