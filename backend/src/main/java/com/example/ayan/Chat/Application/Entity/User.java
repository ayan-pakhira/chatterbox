package com.example.ayan.Chat.Application.Entity;

import com.example.ayan.Chat.Application.Model.FriendListDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexOptions;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.*;

@Document(collection = "user")
@Data
@NoArgsConstructor
public class User {

    @Id
    private ObjectId id;

    @NonNull
    private String userName;


    @NonNull
    private String password;

    @NonNull
    @Indexed(unique = true)
    private String email;

    private List<String> roles;

    private List<ObjectId> friendRequestSent = new ArrayList<>();
    private List<ObjectId> friendRequestReceived = new ArrayList<>();
    private List<ObjectId> friends = new ArrayList<>();


    @DBRef
    private List<PrivateChatMessage> messages = new ArrayList<>();

    @DBRef
    private List<PrivateConversation> chatList = new ArrayList<>();


    private Set<ObjectId> groupChatList = new HashSet<>();


}
