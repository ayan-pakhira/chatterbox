package com.example.ayan.Chat.Application.Entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "private-message")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PrivateChatMessage {

    @Id
    private ObjectId id;

    private String senderId;
    private String senderName;    // sender name

    private String chatId;


    private String receiverId;    // MongoDB ObjectId of receiver (as String)
    private String receiverName;  // receiver name

    private String content;       // Message body

    private Date timeStamp;       // When message was sent
}
