package com.example.ayan.Chat.Application.Repository;
import org.bson.types.ObjectId;

import java.util.*;
public interface UserCustomRepository {

    void removeGroupFromAllUsers(ObjectId groupId);
}
