package com.example.ayan.Chat.Application.Repository;

import com.example.ayan.Chat.Application.Entity.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {
}
