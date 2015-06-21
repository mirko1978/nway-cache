package org.mirko.cache.example.dao;

import org.mirko.cache.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

/**
 * User DAO for CRUD operation with JPA
 */
@Transactional
public interface UserDao extends JpaRepository<User, Long> {

}