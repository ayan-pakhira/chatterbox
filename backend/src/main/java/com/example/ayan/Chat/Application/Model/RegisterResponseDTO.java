package com.example.ayan.Chat.Application.Model;

import lombok.*;
import org.bson.types.ObjectId;

import java.util.*;

@Data
@NoArgsConstructor
public class RegisterResponseDTO {

    private String id;
    private String userName;
    private String email;
    private String token;

    public RegisterResponseDTO(ObjectId id, String email, String userName, String token){
        this.id = id.toHexString();
        this.email = email;
        this.userName = userName;
        this.token = token;
    }
}
