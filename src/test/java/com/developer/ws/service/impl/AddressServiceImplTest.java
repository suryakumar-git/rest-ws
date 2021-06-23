package com.developer.ws.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import com.developer.ws.io.entity.AddressEntity;
import com.developer.ws.io.entity.UserEntity;
import com.developer.ws.io.repositories.AddressRepository;
import com.developer.ws.io.repositories.UserRepository;
import com.developer.ws.shared.dto.AddressDto;

class AddressServiceImplTest {
	
	@InjectMocks
	AddressServiceImpl addressService;
	
	@Mock
	AddressRepository addressRepository;
	
	@Mock
	UserRepository userRepository;
	
	AddressEntity addressEntity;
	
	UserEntity userEntity;
	
	String userId = "kjlh543fd";
	String encryptedPassword = "76jhagsdoij";
	String emailverificationToken = "qwe87jhglkjs";

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
		addressEntity = new AddressEntity();
		addressEntity.setAddressId("asda223sf");
		addressEntity.setCity("Chennai");
		addressEntity.setCountry("India");
		addressEntity.setId(1L);
		addressEntity.setPostalCode("600001");
		addressEntity.setStreetName("Street");
		addressEntity.setType("billing");
		
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
	final void testGetAddress() {
		
		when(addressRepository.findByAddressId(anyString())).thenReturn(addressEntity);
		AddressDto addressDto = addressService.getAddress("asdfasdf3453245");
		
		assertNotNull(addressDto);
		assertEquals(addressDto.getAddressId(), addressEntity.getAddressId());
		assertEquals(addressDto.getType(), addressEntity.getType());
	}
	
	@Test
	final void testGetAddresses() {
		
		when(userRepository.findByUserId(anyString())).thenReturn(userEntity);
		when(addressRepository.findAllByUserDetails(any(UserEntity.class))).thenReturn(getAddressEntity());
		
		List<AddressDto> addresses = addressService.getAddresses(userId);
		
		assertNotNull(addresses);
		assertEquals(addresses.get(0).getAddressId(),getAddressEntity().get(0).getAddressId());
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

