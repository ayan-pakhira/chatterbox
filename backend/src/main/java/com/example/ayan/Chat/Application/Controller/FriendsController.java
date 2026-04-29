package com.example.ayan.Chat.Application.Controller;

import com.example.ayan.Chat.Application.Entity.User;
import com.example.ayan.Chat.Application.Model.FriendListDTO;
import com.example.ayan.Chat.Application.Repository.UserRepository;
import com.example.ayan.Chat.Application.Service.FriendService;
import com.example.ayan.Chat.Application.Service.JwtService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/friends")
@CrossOrigin("http://localhost:5173")
public class FriendsController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendService friendService;

    @Autowired
    private JwtService jwtService;


    @PostMapping("/send-request/{receiverId}")
    public ResponseEntity<?> sentFriendRequest(@PathVariable ObjectId receiverId,
                                               @RequestHeader("Authorization") String authHeader){
        ObjectId senderId = jwtService.extractUserId(authHeader);
        friendService.sendFriendRequest(senderId, receiverId);
        return ResponseEntity.ok("friend request sent");
    }


    @PostMapping("/accept-request/{senderId}")
    public ResponseEntity<FriendListDTO> acceptFriendRequest(@PathVariable ObjectId senderId,
                                                 @RequestHeader("Authorization") String authHeader){
        ObjectId receiverId = jwtService.extractUserId(authHeader);
        FriendListDTO receiver = friendService.acceptFriendRequest(senderId, receiverId);
        return  ResponseEntity.ok(receiver);
    }


    @PostMapping("/reject-request/{senderId}")
    public ResponseEntity<FriendListDTO> rejectFriendRequest(@PathVariable ObjectId senderId,
                                                 @RequestHeader("Authorization") String authHeader){
        ObjectId receiverId = jwtService.extractUserId(authHeader);
        FriendListDTO receiver = friendService.rejectFriendRequest(senderId, receiverId);
        return ResponseEntity.ok(receiver);
    }


    //fetching the friend list, friend request received, friend request sent of a user
    @GetMapping("/friend-list")
    public ResponseEntity<List<FriendListDTO>> getFriendList(@RequestHeader("Authorization") String authHeader){
        ObjectId userId = jwtService.extractUserId(authHeader);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("user not found"));

        List<ObjectId> friendList = user.getFriends();
        List<User> users = userRepository.findAllById(friendList);

//        List<ObjectId> friendRequestReceived = user.getFriendRequestReceived();
//        List<User> userReceived = userRepository.findAllById(friendRequestReceived);

        List<FriendListDTO> userList = users.stream().map(FriendListDTO::new).toList();
        return ResponseEntity.ok(userList);

    }

    @GetMapping("/friend-requests")
    public ResponseEntity<List<FriendListDTO>> getFriendRequestList(@RequestHeader("Authorization") String authHeader){
        ObjectId userId = jwtService.extractUserId(authHeader);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("user not found"));

        List<ObjectId> friendRequests = user.getFriendRequestReceived();
        List<User> users = userRepository.findAllById(friendRequests);

        List<FriendListDTO> userLists = users.stream().map(FriendListDTO::new)
                .toList();

        return ResponseEntity.ok(userLists);
    }
}
