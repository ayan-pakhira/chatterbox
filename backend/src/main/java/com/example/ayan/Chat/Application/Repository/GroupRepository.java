package com.example.ayan.Chat.Application.Repository;

import com.example.ayan.Chat.Application.Entity.Group;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface GroupRepository extends MongoRepository<Group, ObjectId> {

    List<Group> findMembersContainById(ObjectId userId);
    Group findByName(String name);
}
