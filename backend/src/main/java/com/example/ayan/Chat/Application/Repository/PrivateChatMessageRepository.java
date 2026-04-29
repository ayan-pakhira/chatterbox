package com.example.ayan.Chat.Application.Repository;

import com.example.ayan.Chat.Application.Entity.PrivateChatMessage;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrivateChatMessageRepository extends MongoRepository<PrivateChatMessage, ObjectId> {

    @Query("{ 'chatId': ?0 }")
    List<PrivateChatMessage> findConversation(String chatId);

}
