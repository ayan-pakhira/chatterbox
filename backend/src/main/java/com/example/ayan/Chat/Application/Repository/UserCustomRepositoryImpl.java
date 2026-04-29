package com.example.ayan.Chat.Application.Repository;
import com.example.ayan.Chat.Application.Entity.User;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
@Repository
public class UserCustomRepositoryImpl implements UserCustomRepository{

    private final MongoTemplate mongoTemplate;

    public UserCustomRepositoryImpl(MongoTemplate mongoTemplate){
        this.mongoTemplate = mongoTemplate;
    }


    @Override
    public void removeGroupFromAllUsers(ObjectId groupId) {
        Query query = new Query(Criteria.where("groupChatList").is(groupId));
        Update update = new Update().pull("groupChatList", groupId);
        mongoTemplate.updateMulti(query, update, User.class);
    }
}
