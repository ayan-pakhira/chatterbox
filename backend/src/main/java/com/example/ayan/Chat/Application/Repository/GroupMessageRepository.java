package com.example.ayan.Chat.Application.Repository;

import com.example.ayan.Chat.Application.Entity.GroupMessage;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface GroupMessageRepository extends MongoRepository<GroupMessage, ObjectId> {

    List<GroupMessage> findByGroupIdOrderByTimeStampAsc(ObjectId groupId);

    void deleteByGroupIdAndSenderId(ObjectId groupId, ObjectId senderId);

    void deleteByGroupId(ObjectId groupId);

}
