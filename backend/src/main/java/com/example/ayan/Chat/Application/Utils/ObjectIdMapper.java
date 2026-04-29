package com.example.ayan.Chat.Application.Utils;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ObjectIdMapper {

    public ObjectId mapToObjectId(String id){
        return new ObjectId(id);
    }

    public List<ObjectId> mapToObjectIdList(List<String> ids){

        if(ids == null) return List.of();

        return ids.stream()
                .map(ObjectId::new)
                .toList();
    }

    public String mapToString(ObjectId id){
        return id.toHexString();
    }
}
