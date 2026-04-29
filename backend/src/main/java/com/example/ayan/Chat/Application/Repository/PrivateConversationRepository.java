package com.example.ayan.Chat.Application.Repository;

import com.example.ayan.Chat.Application.Entity.PrivateConversation;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrivateConversationRepository extends MongoRepository<PrivateConversation, ObjectId> {

    @Query("{ 'participants': { $all: [ ?0, ?1 ] } }")
    List<PrivateConversation> findByParticipantsPair(String user1Id, String user2Id);



    //find conversation where user is participant
    List<PrivateConversation> findByParticipantsContaining(String userId);

    List<PrivateConversation> findByParticipants(String senderId, String receiverId);


}
