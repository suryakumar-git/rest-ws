package com.developer.ws.io.repositories;

import org.springframework.data.repository.CrudRepository;

import com.developer.ws.io.entity.PasswordResetTokenEntity;

public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetTokenEntity, Long>{

	PasswordResetTokenEntity findByToken(String token);

}
