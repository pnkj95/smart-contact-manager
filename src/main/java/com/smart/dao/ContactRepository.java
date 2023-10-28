package com.smart.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.model.Contact;

public interface ContactRepository extends JpaRepository<Contact, Integer>{
	
	//Page and Pageable is used for Pagination process
	@Query("from Contact as c where c.user.id = :userId")
	public Page<Contact> findContactsByUser(@Param("userId") int id, Pageable pageable);
	
	//Query to get data from search bar
	@Query(value =  "select * from contact c where (c.first_name like %:prefix% or c.second_name like %:prefix% or c.email like %:prefix% or c.phone like %:prefix% or c.work like %:prefix% or c.description like %:prefix%) and c.user_id = :userId", nativeQuery = true)
	public List<Contact> getSearchData(@Param("prefix") String prefix, @Param("userId") Integer id);

}
