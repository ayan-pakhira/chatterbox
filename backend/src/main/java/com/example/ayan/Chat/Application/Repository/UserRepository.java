package com.example.ayan.Chat.Application.Repository;

import com.example.ayan.Chat.Application.Entity.PrivateConversation;
import org.springframework.data.domain.Pageable;


import java.util.*;

import org.springframework.data.domain.Page;
import com.example.ayan.Chat.Application.Entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, ObjectId> {

    User findByUserName(String userName);

    Page<User> findByUserNameContainingIgnoreCase(String query, Pageable pageable);

    User findByEmail(String email);



}
