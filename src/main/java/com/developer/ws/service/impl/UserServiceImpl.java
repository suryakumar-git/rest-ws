package com.developer.ws.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.developer.ws.exceptions.UserServiceException;
import com.developer.ws.io.entity.PasswordResetTokenEntity;
import com.developer.ws.io.entity.UserEntity;
import com.developer.ws.io.repositories.PasswordResetTokenRepository;
import com.developer.ws.io.repositories.UserRepository;
import com.developer.ws.service.EmailService;
import com.developer.ws.service.UserService;
import com.developer.ws.shared.Utils;
import com.developer.ws.shared.dto.AddressDto;
import com.developer.ws.shared.dto.UserDto;
import com.developer.ws.ui.model.response.ErrorMessages;
//import com.developer.ws.ui.model.response.UserRest;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	PasswordResetTokenRepository passwordResetTokenRepository;
	
	@Autowired
	Utils utils;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	EmailService emailService;

	@Override
	public UserDto createUser(UserDto user) {

		if (userRepository.findByEmail(user.getEmail()) != null)
			throw new UserServiceException(ErrorMessages.RECORD_ALREADY_EXISTS.getErrorMessage());

		for (int i = 0; i < user.getAddresses().size(); i++) {
			AddressDto address = user.getAddresses().get(i);
			address.setUserDetails(user);
			address.setAddressId(utils.generateAddressId(30));
			user.getAddresses().set(i, address);
		}

		// UserEntity userEntity = new UserEntity();
		// BeanUtils.copyProperties(user, userEntity);
		ModelMapper modelMapper = new ModelMapper();
		UserEntity userEntity = modelMapper.map(user, UserEntity.class);

		String publicUserId = utils.generateUserId(30);
		userEntity.setUserId(publicUserId);
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		userEntity.setEmailVerificationToken(utils.generateEmailVerificationToken(publicUserId));
		userEntity.setEmailVerificationStatus(false);

		UserEntity storedUserDetails = userRepository.save(userEntity);

		// UserDto returnValue = new UserDto();
		// BeanUtils.copyProperties(storedUserDetails, returnValue);
		UserDto returnValue = modelMapper.map(storedUserDetails, UserDto.class);

		emailService.sendVerificationMail(returnValue);

		return returnValue;
	}

	@Override
	public UserDto getUser(String email) {

		UserEntity userEntity = userRepository.findByEmail(email);

		if (userEntity == null)
			throw new UsernameNotFoundException(email);

		UserDto returnValue = new UserDto();
		
		/*
		 * ModelMapper modelMapper = new ModelMapper(); returnValue =
		 * modelMapper.map(userEntity, UserDto.class);
		 */
		BeanUtils.copyProperties(userEntity, returnValue);
		return returnValue;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		UserEntity userEntity = userRepository.findByEmail(email);

		if (userEntity == null)
			throw new UsernameNotFoundException(email);

		// return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new
		// ArrayList<>());
		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(),
				userEntity.getEmailVerificationStatus(), true, true, true, new ArrayList<>());

	}

	@Override
	public UserDto getUserByUserId(String userId) {

		UserDto returnValue = new UserDto();
		UserEntity userEntity = userRepository.findByUserId(userId);

		if (userEntity == null)
			throw new UsernameNotFoundException(userId);
		
		ModelMapper modelMapper = new ModelMapper();
		returnValue = modelMapper.map(userEntity, UserDto.class);

		//BeanUtils.copyProperties(userEntity, returnValue);
		return returnValue;
	}

	@Override
	public UserDto updateUser(String userId, UserDto user) {

		UserDto returnValue = new UserDto();
		UserEntity userEntity = userRepository.findByUserId(userId);
		if (userEntity == null)
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

		userEntity.setFirstName(user.getFirstName());
		userEntity.setLastName(user.getLastName());

		UserEntity updatedUserDetails = userRepository.save(userEntity);
		
		ModelMapper modelMapper = new ModelMapper();
		returnValue = modelMapper.map(updatedUserDetails, UserDto.class);

		//BeanUtils.copyProperties(updatedUserDetails, returnValue);
		return returnValue;
	}

	@Override
	public void deleteUser(String userId) {

		UserEntity userEntity = userRepository.findByUserId(userId);
		if (userEntity == null)
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

		userRepository.delete(userEntity);
	}

	@Override
	public List<UserDto> getUsers(int page, int limit) {

		List<UserDto> returnValue = new ArrayList<>();

		if (page > 0)
			page -= 1;

		Pageable pageableRequest = PageRequest.of(page, limit);

		Page<UserEntity> usersPage = userRepository.findAll(pageableRequest);
		List<UserEntity> users = usersPage.getContent();

		for (UserEntity userEntity : users) {
			UserDto userDto = new UserDto();
			BeanUtils.copyProperties(userEntity, userDto);
			returnValue.add(userDto);
		}
		return returnValue;
	}

	@Override
	public boolean requestPasswordReset(String email) {

		boolean returnValue = false;

		UserEntity userEntity = userRepository.findByEmail(email);

		if (userEntity == null) {
			return returnValue;
		}
		String token = utils.generatePasswordResetToken(userEntity.getUserId());

		PasswordResetTokenEntity passwordResetTokenEntity = new PasswordResetTokenEntity();
		passwordResetTokenEntity.setToken(token);
		passwordResetTokenEntity.setUserDetails(userEntity);
		passwordResetTokenRepository.save(passwordResetTokenEntity);
		
		returnValue = emailService.sendPasswordResetRequest(userEntity, token);

		return returnValue;
	}

	@Override
	public boolean verifyEmailToken(String token) {

		boolean returnValue = false;

		UserEntity userEntity = userRepository.findUserByEmailVerificationToken(token);

		if (userEntity != null) {
			boolean hastokenExpired = Utils.hasTokenExpired(token);
			if (!hastokenExpired) {
				userEntity.setEmailVerificationToken(null);
				userEntity.setEmailVerificationStatus(Boolean.TRUE);
				userRepository.save(userEntity);
				returnValue = true;
			}
		}

		return returnValue;
	}

	@Override
	public boolean resetPassword(String token, String password) {
		
		boolean returnValue = false;
		
		if(Utils.hasTokenExpired(token)) {
			return returnValue;
		}
		
		PasswordResetTokenEntity passwordResetTokenEntity = passwordResetTokenRepository.findByToken(token);
		
		if (passwordResetTokenEntity == null) {
			return returnValue;
		}
		
		String encodedPassword = bCryptPasswordEncoder.encode(password);
		
		UserEntity userEntity = passwordResetTokenEntity.getUserDetails();
		userEntity.setEncryptedPassword(encodedPassword);
		
		UserEntity savedUserEntity =  userRepository.save(userEntity);
		
		if(savedUserEntity != null && savedUserEntity.getEncryptedPassword().equalsIgnoreCase(encodedPassword)) {
			returnValue = true;
		}
		
		passwordResetTokenRepository.delete(passwordResetTokenEntity);
		return returnValue;
	}

}
