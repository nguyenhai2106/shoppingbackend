package com.donations.admin.user;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.donations.admin.paging.PagingAndSortingHelper;
import com.donations.common.entity.Role;
import com.donations.common.entity.User;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService {
	public static final int USERS_PER_PAGE = 10;
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public List<User> listAll() {
		return (List<User>) userRepository.findAll();
	}

	public void listByPage(int pageNum, PagingAndSortingHelper helper) {
		helper.listEntities(pageNum, USERS_PER_PAGE, userRepository);
	}

	public List<Role> listRoles() {
		return (List<Role>) roleRepository.findAll();
	}

	public User save(User user) {
		boolean isUpdatingUser = (user.getId() != null);
		if (isUpdatingUser) {
			User existingUser = userRepository.findById(user.getId()).get();
			if (user.getPassword().isEmpty()) {
				user.setPassword(existingUser.getPassword());
			} else {
				encodePassword(user);
			}
		} else {
			encodePassword(user);
		}
		return userRepository.save(user);
	}

	public void encodePassword(User user) {
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
	}

	public boolean isEmailUnique(Integer id, String email) {
		User userByEmail = userRepository.getUserByEmail(email);
		if (userByEmail == null) {
			return true;
		}
		boolean isCreatingNew = (id == null);
		if (isCreatingNew) {
			if (userByEmail != null) {
				return false;
			}
		} else {
			if (userByEmail.getId() != id) {
				return false;
			}
		}
		return true;
	}

	public User get(Integer id) throws UserNotFoundException {
		try {
			return userRepository.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new UserNotFoundException("Could not find any user with ID " + id);
		}
	}

	public void delete(Integer id) throws UserNotFoundException {
		Long countById = userRepository.countById(id);
		if (countById == null || countById == 0) {
			throw new UserNotFoundException("Could not find any user with ID " + id);
		}
		userRepository.deleteById(id);
	}

	public void updateUserEnabledStatus(Integer id, boolean enabled) {
		userRepository.updateEnabledStatus(id, enabled);
	}

	public User getByEmail(String email) {
		return userRepository.getUserByEmail(email);
	}

	public User updateAccount(User userInform) {
		User userInDB = userRepository.findById(userInform.getId()).get();
		if (!userInform.getPassword().isEmpty()) {
			userInDB.setPassword(userInform.getPassword());
			encodePassword(userInDB);
		}
		if (userInform.getPhotos() != null) {
			userInDB.setPhotos(userInform.getPhotos());
		}
		userInDB.setFirstName(userInform.getFirstName());
		userInDB.setLastName(userInform.getLastName());
		return userRepository.save(userInDB);
	}

}
