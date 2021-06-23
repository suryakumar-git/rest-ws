package com.developer.ws.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.developer.ws.exceptions.UserServiceException;
import com.developer.ws.io.entity.AddressEntity;
import com.developer.ws.io.entity.UserEntity;
//import com.developer.ws.io.repositories.PasswordResetTokenRepository;
import com.developer.ws.io.repositories.UserRepository;
import com.developer.ws.shared.Utils;
import com.developer.ws.shared.dto.AddressDto;
import com.developer.ws.shared.dto.UserDto;

class UserServiceImplTest {
	
	@InjectMocks
	UserServiceImpl userService;
	
	@Mock
	EmailServiceImpl emailService;
	
	@Mock
	UserRepository userRepository;
	
	@Mock
	Utils utils;

	@Mock
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	String userId = "kjlh543fd";
	String encryptedPassword = "76jhagsdoij";
	String emailverificationToken = "qwe87jhglkjs";
	
	UserEntity userEntity;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
		userEntity = new UserEntity();
		userEntity.setId(1L);
		userEntity.setFirstName("Surya");
		userEntity.setLastName("Kumar");
		userEntity.setUserId(userId);
		userEntity.setEncryptedPassword(encryptedPassword);
		userEntity.setEmail("sk@test.com");
		userEntity.setEmailVerificationToken(emailverificationToken);
		userEntity.setAddresses(getAddressEntity());
	}

	@Test
	final void testGetUser() {

		when( userRepository.findByEmail( anyString() ) ).thenReturn( userEntity );
		
		UserDto userDto = userService.getUser("sk@test.com");
		
		assertNotNull(userDto);
		assertEquals("Surya", userDto.getFirstName());
	}
	
	@Test
	final void testGetUser_UsernameNotFoundException() {
		
		when( userRepository.findByEmail( anyString() ) ).thenReturn(null);
		
		
		assertThrows(UsernameNotFoundException.class, 
				()-> {
					userService.getUser("sk@test.com");
				});
	}
	
	@Test
	final void testCreateUser_CreateUserServiceException() {
		
		when(userRepository.findByEmail(anyString())).thenReturn(userEntity);
		
		UserDto userDto = new UserDto();		
		userDto.setAddresses(getAddressesDto());
		userDto.setFirstName("Surya");
		userDto.setLastName("Kumar");
		userDto.setPassword("13264578");
		userDto.setEmail("sk@test.com");
		
		assertThrows(UserServiceException.class, 
				()-> {
					userService.createUser(userDto);
				});
	}
	
	@Test
	final void testCreateUser() {
		
		when( userRepository.findByEmail( anyString() ) ).thenReturn( null );
		when(utils.generateAddressId(anyInt())).thenReturn("iu07hiuyucd");
		when(utils.generateUserId(anyInt())).thenReturn(userId);
		when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encryptedPassword);
		when(utils.generateEmailVerificationToken(anyString())).thenReturn(emailverificationToken);
		when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
		Mockito.doNothing().when(emailService).sendVerificationMail(any(UserDto.class));
		
		UserDto userDto = new UserDto();		
		userDto.setAddresses(getAddressesDto());
		userDto.setFirstName("Surya");
		userDto.setLastName("Kumar");
		userDto.setPassword("13264578");
		userDto.setEmail("sk@test.com");
		
		UserDto storedUserDetails = userService.createUser(userDto);
		assertNotNull(storedUserDetails);
		assertEquals(userEntity.getFirstName(), storedUserDetails.getFirstName());
		assertEquals(userEntity.getLastName(), storedUserDetails.getLastName());
		assertNotNull(storedUserDetails.getUserId());
		assertEquals(storedUserDetails.getAddresses().size(), userEntity.getAddresses().size());
		verify(utils,times(2)).generateAddressId(30);
		verify(bCryptPasswordEncoder,times(1)).encode("13264578");
		verify(userRepository,times(1)).save(any(UserEntity.class));
	}
	
	private List<AddressDto> getAddressesDto() {
		
		AddressDto addressDto = new AddressDto();
		addressDto.setType("shipping");
		addressDto.setCity("Chennai");
		addressDto.setCountry("India");
		addressDto.setPostalCode("600001");
		addressDto.setStreetName("First Avenue");
		
		AddressDto billingAddressDto = new AddressDto();
		addressDto.setType("billing");
		billingAddressDto.setCity("Chennai");
		billingAddressDto.setCountry("India");
		billingAddressDto.setPostalCode("600001");
		billingAddressDto.setStreetName("First Avenue");
		
		List<AddressDto> addresses = new ArrayList<>();
		addresses.add(addressDto);
		addresses.add(billingAddressDto);
		return addresses;
		
	}
	
	private List<AddressEntity> getAddressEntity() {
		
		List<AddressDto> addresses = getAddressesDto();
		
		Type listType = new TypeToken<List<AddressEntity>>() {}.getType();
		
		return new ModelMapper().map(addresses,listType);
	}

}
