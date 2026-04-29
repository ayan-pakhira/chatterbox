package com.example.ayan.Chat.Application.Entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Data
@Document(collection = "group")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Group {

    @Id
    private ObjectId id;

    private String name;

    private ObjectId createdBy;

    private String lastMessage;

    private Set<ObjectId> members = new HashSet<>();

    private Set<ObjectId> admins = new HashSet<>();

    private Date createdAt = new Date();
}
