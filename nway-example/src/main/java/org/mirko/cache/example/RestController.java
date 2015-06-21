package org.mirko.cache.example;

import org.mirko.cache.example.dao.NotFoundException;
import org.mirko.cache.example.model.User;
import org.mirko.cache.example.service.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Rest controller for the User api
 */
@Controller
@RequestMapping("/users")
public class RestController {

	@Autowired
	private UserServices userServices;

	/**
	 * Add a new user to the db
	 *
	 * @param user user to add (id is ignored)
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public
	@ResponseBody
	void createUser(@RequestBody User user) {
		userServices.save(user);
	}

	/**
	 * Update an existing user details
	 *
	 * @param user user to update
	 * @param id     user id
	 * @throws NotFoundException if the user doesn't exist
	 */
	@RequestMapping(value = "{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public
	@ResponseBody
	void updateUser(@RequestBody User user, @PathVariable("id") long id) throws NotFoundException {
		User userFound = userServices.findById(id);
		if (userFound == null) {
			throw new NotFoundException("User id " + id + " not found");
		}
		userFound.setEmail(user.getEmail());
		userFound.setFirstName(user.getFirstName());
		userFound.setLastName(user.getLastName());
		userFound.setPhone(user.getPhone());
		userServices.save(userFound);
	}

	/**
	 * Get a user from the id
	 *
	 * @param id user id
	 * @return the user
	 * @throws NotFoundException if the user doesn't exist
	 */
	@RequestMapping(value = "{id}", method = RequestMethod.GET)
	public
	@ResponseBody
	User getUser(@PathVariable("id") long id) throws NotFoundException {
		return userServices.findById(id);
	}

	/**
	 * Get all the users stored in the database
	 *
	 * @return user list
	 */
	@RequestMapping(method = RequestMethod.GET)
	public
	@ResponseBody
	List<User> getAllUsers() {
		return userServices.getAll();
	}

	/**
	 * Delete a user from the database
	 *
	 * @param id user id to delete
	 * @throws NotFoundException if the user doesn't exist
	 */
	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public
	@ResponseBody
	void deleteUser(@PathVariable("id") long id) throws NotFoundException {
		userServices.delete(id);
	}

	/**
	 * Load and save users from names.txt
	 */
	@RequestMapping(value = "/load", method = RequestMethod.POST)
	public
	@ResponseBody
	void loadUsers() {
		userServices.loadUsers();
	}

	/**
	 * When a {@link NotFoundException} is raised then the response is a 404
	 *
	 * @param response http response
	 * @throws IOException
	 */
	@ExceptionHandler(NotFoundException.class)
	void handleBadRequests(HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.NOT_FOUND.value(), "User not found");
	}
}
