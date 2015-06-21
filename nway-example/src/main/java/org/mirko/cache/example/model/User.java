package org.mirko.cache.example.model;

import com.google.common.base.MoreObjects;

import javax.persistence.*;
import java.io.Serializable;

/**
 * User entity
 */
@Entity
@Table(name = "users")
public class User implements Serializable {

	private static final long serialVersionUID = 6342164008595345063L;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "email")
	private String email;

	@Column(name = "phone")
	private String phone;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("Id", id)
			.add("First name", firstName)
			.add("Last name", lastName)
			.add("Phone", phone)
			.add("Email", email)
			.toString();
	}

}
