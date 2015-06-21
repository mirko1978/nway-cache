package org.mirko.cache.example.service;

import org.mirko.cache.example.dao.NotFoundException;
import org.mirko.cache.example.model.User;

import java.util.List;

/**
 * Service layer for CRUD operations around a User
 */
public interface UserServices {
	/**
	 * Save a user if the id is specified and exist, otherwise insert a new one
	 *
	 * @param user the user
	 */
	void save(User user);

	/**
	 * Get a user from Id
	 *
	 * @param id the user id
	 * @return the user or null if nothing is found
	 */
	User findById(long id) throws NotFoundException;

	/**
	 * Get all the user in the database
	 *
	 * @return the user list
	 */
	List<User> getAll();

	/**
	 * Delete a user from the database
	 *
	 * @param id the user id
	 * @throws NotFoundException if the id doesn't exist in the database
	 */
	void delete(long id) throws NotFoundException;

	/**
	 * Generate and save a user for each line in names.txt
	 */
	void loadUsers();
}