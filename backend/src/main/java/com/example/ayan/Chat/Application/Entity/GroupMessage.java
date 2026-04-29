package com.example.ayan.Chat.Application.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Data
@Document(collection = "group-message")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMessage {

    @Id
    private ObjectId id;

    private ObjectId groupId;

    private ObjectId senderId;

    private String content;

    private Date timeStamp = new Date();



}
