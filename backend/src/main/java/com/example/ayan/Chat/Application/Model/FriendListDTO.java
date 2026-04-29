package com.example.ayan.Chat.Application.Model;
import com.example.ayan.Chat.Application.Entity.User;
import lombok.*;
import org.bson.types.ObjectId;

import java.util.*;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class FriendListDTO {

    private String id;
    private String userName;
    private String email;


    private List<String> friendRequestReceived;
    private List<String> friends;

    public FriendListDTO(User user){
        this.id = user.getId().toHexString();
        this.email = user.getEmail();
        this.userName = user.getUserName();

        this.friendRequestReceived = user.getFriendRequestReceived().stream()
                .map(ObjectId::toHexString)
                .collect(Collectors.toList());

        this.friends = user.getFriends().stream()
                .map(ObjectId::toHexString)
                .collect(Collectors.toList());
    }
}
