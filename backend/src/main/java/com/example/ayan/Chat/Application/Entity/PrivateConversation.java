package com.example.ayan.Chat.Application.Entity;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Document(collection = "private-conversation")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PrivateConversation {

    @Id
    private ObjectId id;

    private List<String> participants;

    private String lastMessage;

    private Date updatedAt = new Date();

    @DBRef
    List<PrivateChatMessage> messages = new ArrayList<>();
}
