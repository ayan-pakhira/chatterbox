package com.example.ayan.Chat.Application.Model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Document(collection = "email-logs")
@Data
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class EmailAuth {


    @Id
    private ObjectId id;

    private String toUser;

    private String subject;

    private String body;

    private boolean sent;

    private LocalDateTime sentAt;


    public EmailAuth(String toEmail, String body, LocalDateTime sentAt) {
        this.toUser = toEmail;
        this.body = body;
        this.sentAt = sentAt;
    }
}

