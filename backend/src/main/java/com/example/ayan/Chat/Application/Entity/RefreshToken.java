package com.example.ayan.Chat.Application.Entity;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.*;

@Document(collection = "refresh-token")
@Data
@Getter
@Setter
@Builder
public class RefreshToken {

    @Id
    private String refreshToken;

    private String userId;

    private Instant createdAt;

    private Instant expiry;

    private boolean revoked;

    @DBRef
    private User user;

}
