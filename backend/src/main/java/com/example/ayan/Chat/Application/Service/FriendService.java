package com.example.ayan.Chat.Application.Service;

import com.example.ayan.Chat.Application.Entity.User;
import com.example.ayan.Chat.Application.Model.FriendListDTO;
import com.example.ayan.Chat.Application.Repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FriendService {


    @Autowired
    private UserRepository userRepository;


    public void sendFriendRequest(ObjectId senderId, ObjectId receiverId){

        if(senderId.equals(receiverId)){
            throw new IllegalArgumentException("you cannot send request to yourself");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("sender not found"));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("receiver not found"));

        if(sender.getFriends().contains(receiverId)){
            throw new IllegalArgumentException("user is already added into the list");
        }

        if(sender.getFriendRequestSent().contains(receiverId)){
            throw new IllegalArgumentException("Friend request already sent");
        }

        sender.getFriendRequestSent().add(receiverId);
        receiver.getFriendRequestReceived().add(senderId);

        userRepository.save(sender);
        userRepository.save(receiver);
    }


    public FriendListDTO acceptFriendRequest(ObjectId senderId, ObjectId receiverId){
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("receiver not found"));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("sender not found"));

        if(!receiver.getFriendRequestReceived().contains(senderId)){
            throw new IllegalArgumentException("no friend request available from this user");
        }

        //after accepting the request the friend request will be removed from the pending list of both.
        sender.getFriendRequestSent().remove(receiverId);
        receiver.getFriendRequestReceived().remove(senderId);

        //now add the user to friend list of both the users.
        sender.getFriends().add(receiverId);
        receiver.getFriends().add(senderId);

        userRepository.save(sender);
        userRepository.save(receiver);

        return new FriendListDTO(receiver);
    }


    public FriendListDTO rejectFriendRequest(ObjectId senderId, ObjectId receiverId){
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("not found"));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("not found"));

        receiver.getFriendRequestReceived().remove(senderId);
        sender.getFriendRequestSent().remove(receiverId);

        userRepository.save(sender);
        userRepository.save(receiver);

        return new FriendListDTO(receiver);
    }
}
