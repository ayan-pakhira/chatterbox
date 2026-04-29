package com.example.ayan.Chat.Application.Repository;

import com.example.ayan.Chat.Application.Entity.PasswordResetToken;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository extends MongoRepository<PasswordResetToken, ObjectId> {

    PasswordResetToken findByEmail(String email);
    void deleteByEmail(String email);
}
