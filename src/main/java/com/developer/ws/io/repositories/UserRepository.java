package com.developer.ws.io.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.developer.ws.io.entity.UserEntity;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long> {
	UserEntity findByEmail(String email);
	UserEntity findByUserId(String userId);
	UserEntity findUserByEmailVerificationToken(String token);
	
	@Query(value="select * from users u where u.EMAIL_VERIFICATION_STATUS = 'true'",
			countQuery="select count(*) from users u where u.EMAIL_VERIFICATION_STATUS = 'true'", 
			nativeQuery = true)
	Page<UserEntity> findAllUsersWithConfirmedEmailAddress(Pageable pageableRequest);
	
	//Positional parameter
	@Query(value="select * from users u where u.first_name =?1", nativeQuery = true)
	List<UserEntity> findUserByFirstName(String firstName);
	
	//Named parameter
	@Query(value="select * from users u where u.last_name =:lastName", nativeQuery = true)
	List<UserEntity> findUserByLastName(@Param("lastName") String lastName);
	
	//LIKE expression
	@Query(value="select * from users u where u.first_name LIKE %:keyword% or u.last_name LIKE %:keyword%", nativeQuery = true)
	List<UserEntity> findUsersByKeyword(@Param("keyword") String keyword);
	
	//LIKE expression
	@Query(value="select u.first_name ,u.last_name from users u where u.first_name LIKE %:keyword% or u.last_name LIKE %:keyword%", nativeQuery = true)
	List<Object[]> findUserFirsNameAndLastNameByKeyword(@Param("keyword") String keyword);
	
	//UPDATE Query and DELETE query needs @Transactional and @Modifying annotations
	@Transactional
	@Modifying
	@Query(value="update users u set u.EMAIL_VERIFICATION_STATUS = :emailVerificationStatus where u.user_id=:userId", nativeQuery = true)
	void updateUserEmailVerificationStatus(@Param("emailVerificationStatus") boolean emailVerificationStatus, @Param("userId") String userId);
	
}
