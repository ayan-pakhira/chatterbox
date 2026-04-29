package com.example.ayan.Chat.Application.Model;
import lombok.*;
import org.bson.types.ObjectId;

import java.util.*;

@Data
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class SearchApiDTO {

    private String id;
    private String userName;
    private String email;
    


    public SearchApiDTO(ObjectId id, String userName, String email) {
        this.id = id.toHexString();
        this.email = email;
        this.userName = userName;
    }
}
