package com.developer.ws.ui.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.developer.ws.service.impl.UserServiceImpl;
import com.developer.ws.shared.dto.AddressDto;
import com.developer.ws.shared.dto.UserDto;
import com.developer.ws.ui.model.response.UserRest;

class UserControllerTest {

	@InjectMocks
	UserController userController;

	@Mock
	UserServiceImpl userService;
	
	UserDto userDto;
	
	final String USER_ID = "qwe12ef4g3gv";

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
		
		userDto = new UserDto();
		userDto.setFirstName("Surya");
		userDto.setLastName("Kumar");
		userDto.setEmail("sk@test.com");
		userDto.setEmailVerificationToken(null);
		userDto.setEmailVerificationStatus(Boolean.FALSE);
		userDto.setUserId(USER_ID);
		userDto.setAddresses(getAddressesDto());
		userDto.setEncryptedPassword("klhjgfdg564gd");
		
	}

	@Test
	final void testGetUser() {
		when(userService.getUserByUserId(anyString())).thenReturn(userDto);
		
		UserRest userRest = userController.getUser(USER_ID);
		
		assertNotNull(userRest);
		assertEquals(USER_ID, userRest.getUserId());
		assertEquals(userDto.getFirstName(), userRest.getFirstName());
		assertEquals(userDto.getLastName(), userRest.getLastName());
		assertEquals(userDto.getAddresses().size(), userRest.getAddresses().size());
		
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

}
