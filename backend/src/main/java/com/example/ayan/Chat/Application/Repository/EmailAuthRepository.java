package com.example.ayan.Chat.Application.Repository;
import com.example.ayan.Chat.Application.Model.EmailAuth;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface EmailAuthRepository extends MongoRepository<EmailAuth, ObjectId> {
}
