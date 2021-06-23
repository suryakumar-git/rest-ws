package com.developer.ws.io.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.developer.ws.io.entity.AddressEntity;
import com.developer.ws.io.entity.UserEntity;
import com.developer.ws.io.repositories.UserRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserRepositoryTest {
	
	@Autowired
	UserRepository userRepository;
	
	static boolean recordsCreated = false;

	@BeforeEach
	void setUp() throws Exception {
		if(!recordsCreated)
		createRecord();
	}

	@Test
	final void testGetVerifiedUsers() {
		Pageable pageableRequest = PageRequest.of(0, 2);
		Page<UserEntity> pages = userRepository.findAllUsersWithConfirmedEmailAddress(pageableRequest);
		assertNotNull(pages);
		
		List<UserEntity> userEntities = pages.getContent();
		assertNotNull(userEntities);
		assertTrue(userEntities.size() == 1);
	}
	
	@Test
	final void testFindUserByFirstName() {
		String firstName ="Surya";
		List <UserEntity> users = userRepository.findUserByFirstName(firstName);
		
		assertNotNull(users);
		assertTrue(users.size() == 1);
		
		UserEntity user = users.get(0);
		assertEquals(firstName, user.getFirstName());
		
	}
	
	@Test
	final void testFindUserByLastName() {
		String lastName ="Kumar";
		List <UserEntity> users = userRepository.findUserByLastName(lastName);
		
		assertNotNull(users);
		assertTrue(users.size() == 1);
		
		UserEntity user = users.get(0);
		assertTrue(user.getLastName().equals(lastName));
		
	}
	
	@Test
	final void testFindUsesrByKeyword() {
		String keyword ="Kum";
		List <UserEntity> users = userRepository.findUsersByKeyword(keyword); 
		
		assertNotNull(users);
		assertTrue(users.size() == 1);
		
		UserEntity user = users.get(0);
		assertTrue(user.getLastName().contains(keyword) || user.getFirstName().contains(keyword));
		
	}
	
	@Test
	final void testFindUserFirsNameAndLastNameByKeyword() {
		String keyword ="Kum";
		List <Object[]> users = userRepository.findUserFirsNameAndLastNameByKeyword(keyword); 
		
		assertNotNull(users);
		assertTrue(users.size() == 1);
		
		Object[] user = users.get(0);
		String userFirstName = (String) user[0];
		String userLastName = (String) user[1];
		
		assertNotNull(userFirstName);
		assertNotNull(userLastName);
		
		System.out.println(userFirstName);
		System.out.println(userLastName);
	}
	
	@Test
	final void testUpdateUserEmailVerificationStatus() {
		boolean newEmailVerificationStatus = false;
		userRepository.updateUserEmailVerificationStatus(newEmailVerificationStatus, "123adsf");
		
		UserEntity storedUserDetails = userRepository.findByUserId("123adsf");
		
		boolean storedEmailVerificationStatus = storedUserDetails.getEmailVerificationStatus();
		
		assertTrue(storedEmailVerificationStatus == newEmailVerificationStatus);
	}
	
	private void createRecord() {
		UserEntity userEntity = new UserEntity();
		userEntity.setFirstName("Surya");
		userEntity.setLastName("Kumar");
		userEntity.setUserId("123adsf");
		userEntity.setEncryptedPassword("asd322afdas");
		userEntity.setEmail("sk@test.com");
		userEntity.setEmailVerificationStatus(true);
		
		AddressEntity addressEntity = new AddressEntity();
		addressEntity.setAddressId("asda223sf");
		addressEntity.setCity("Chennai");
		addressEntity.setCountry("India");
		addressEntity.setPostalCode("600001");
		addressEntity.setStreetName("Street");
		addressEntity.setType("Shipping");
		
		List<AddressEntity> addresses = new ArrayList<>();
		addresses.add(addressEntity);
		
		userEntity.setAddresses(addresses);;
		
		userRepository.save(userEntity);
		
		recordsCreated = true;
	}

}
