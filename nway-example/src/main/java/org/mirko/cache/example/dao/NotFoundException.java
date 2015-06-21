package org.mirko.cache.example.dao;

/**
 * Data not found inside the database
 */
public class NotFoundException extends Exception {
	/**
	 * Create a new instance of NotFoundException with the message cause
	 * @param message cause
	 */
	public NotFoundException(String message) {
		super(message);
	}
}
