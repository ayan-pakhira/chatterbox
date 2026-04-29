package com.example.ayan.Chat.Application.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.*;

@Data
@NoArgsConstructor
public class LoginResponseDTO {

    private String id;
    private String userName;
    private String email;
    private String token;

    public LoginResponseDTO(ObjectId id, String email, String token, String userName){
        this.id = id.toHexString();
        this.email = email;
        this.token = token;
        this.userName = userName;
    }
}
