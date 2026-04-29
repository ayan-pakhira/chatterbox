package com.example.ayan.Chat.Application.Entity;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.*;

@Document(collection = "otp-token")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PasswordResetToken {

    @Id
    private ObjectId id;

    private String email;
    private String otp;
    private LocalDateTime expiryTime;

    public PasswordResetToken(String email, String otp, LocalDateTime expiryTime){
        this.email = email;
        this.otp = otp;
        this.expiryTime = expiryTime;
    }
}
