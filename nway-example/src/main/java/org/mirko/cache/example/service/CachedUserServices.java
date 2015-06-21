package org.mirko.cache.example.service;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import org.mirko.cache.example.dao.NotFoundException;
import org.mirko.cache.example.dao.UserDao;
import org.mirko.cache.example.model.User;
import org.mirko.cache.nway.Cache;
import org.mirko.cache.nway.CacheLoaderException;
import org.mirko.cache.nway.NWayCacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

/**
 * <p>Implementation with cache of DataService</p>
 * <p>The bean is stored in the application context as singleton as default behaviour from spring</p>
 */
@Service
public class CachedUserServices implements UserServices {
	private static final Logger LOG = LoggerFactory.getLogger(CachedUserServices.class);
	private static final String NOT_FOUND_MSG = "User not found for id ";
	private static final String NAME_LIST = "names.txt";
	private Cache<Long, User> cache;

	@Autowired
	private UserDao userDao;

	/**
	 * Instanciate a new DataServiceChaced and the cache object
	 */
	@PostConstruct
	public void init() {
		cache = new NWayCacheBuilder<Long, User>().build(id -> {
			User p = userDao.findOne(id);
			if (p == null) {
				throw new NotFoundException(NOT_FOUND_MSG + id);
			}
			return p;
		});
	}

	@Override
	public void save(User user) {
		userDao.save(user);
		cache.put(user.getId(), user);
		LOG.info("Saved {}", user);
	}

	@Override
	public User findById(long id) throws NotFoundException {
		User user = null;
		try {
			user = cache.get(id);
		} catch (CacheLoaderException nfe) {
			throw new NotFoundException(NOT_FOUND_MSG + id);
		}
		LOG.info("Getting user by id {}: {}", id, user);
		return user;
	}

	@Override
	public List<User> getAll() {
		List<User> users = ImmutableList.copyOf(userDao.findAll());
		LOG.info("Get users list: [{}]", Joiner.on(",").join(users));
		return users;
	}

	@Override
	public void delete(long id) throws NotFoundException {
		cache.remove(id);
		try {
			userDao.delete(id);
		} catch (EmptyResultDataAccessException e) {
			throw new NotFoundException(NOT_FOUND_MSG + id);
		}
		LOG.info("Deleted user by id {}", id);
	}

	@Override
	public void loadUsers() {
		try {
			Resources.readLines(Resources.getResource(NAME_LIST), Charsets.UTF_8)
				.forEach(line -> {
					User user = new User();
					user.setFirstName(line.substring(0, line.indexOf(' ')).trim());
					user.setLastName(line.substring(line.indexOf(' ') + 1, line.length())
						.replaceAll("[^a-zA-Z]", ""));
					user.setEmail(user.getFirstName().toLowerCase() + "."
						+ user.getLastName().toLowerCase() + "@mirko.org");
					user.setPhone(String.format("0%010d", Math.round(Math.random() * 10000000)));
					save(user);
				});
		} catch (IOException e) {
			LOG.error("Cannot load names.txt", e);
		}
	}

}
