package com.example.ayan.Chat.Application.Controller;

import com.example.ayan.Chat.Application.Entity.Group;
import com.example.ayan.Chat.Application.Entity.GroupMessage;
import com.example.ayan.Chat.Application.Entity.User;
import com.example.ayan.Chat.Application.Model.GroupMessageDTO;
import com.example.ayan.Chat.Application.Repository.UserRepository;
import com.example.ayan.Chat.Application.Service.GroupMessageService;
import com.example.ayan.Chat.Application.Service.GroupService;
import com.example.ayan.Chat.Application.Service.JwtService;
import com.example.ayan.Chat.Application.Service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/group-message")
public class GroupMessageController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private GroupMessageService groupMessageService;


    @Autowired
    private GroupService groupService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    //send the messages through the websocket
    //if it do not work in this way, then try with the annotation
    //@SendTo
    @MessageMapping("/group.sendMessage") //from client /app/group.sendMessages
    public GroupMessageDTO sendMessages(@Payload GroupMessage messagePayload){

        GroupMessage message = new GroupMessage();
        message.setGroupId(messagePayload.getGroupId());
        message.setSenderId(messagePayload.getSenderId());
        message.setContent(messagePayload.getContent());
        message.setTimeStamp(new Date());

        GroupMessage saved = groupMessageService.saveMessage(message);
        User sender = userRepository.findById(messagePayload.getSenderId())
                .orElseThrow(() -> new RuntimeException("sender not found"));
        GroupMessageDTO dto = new GroupMessageDTO(saved, sender);

        String groupHexId = saved.getGroupId().toHexString();

        messagingTemplate.convertAndSend("/topic/group/" + groupHexId, dto);
        return dto;
    }

    //saving the send messages in the database
    @PostMapping("/send")
    public ResponseEntity<?> saveGroupMessage(@RequestBody Map<String, String> body,
                                              @RequestHeader("Authorization") String authHeader){

        ObjectId senderId = jwtService.extractUserId(authHeader);


        ObjectId groupId = new ObjectId(body.get("groupId"));
        String content = body.get("content");

        GroupMessage message = new GroupMessage();
        message.setGroupId(groupId);
        message.setSenderId(senderId);
        message.setContent(content);

        groupMessageService.saveMessage(message);

        return ResponseEntity.ok("message saved");

    }


    //fetching the saved messages from the database
    @GetMapping("/get-message/{groupId}")
    public ResponseEntity<?> getMessages(@PathVariable String groupId,
                                         @RequestParam(value = "page", defaultValue = "0", required = false)int page,
                                         @RequestParam(value = "size", defaultValue = "20", required = false)int size){
        List<GroupMessage> messages = groupMessageService.getMessage(new ObjectId(groupId));

        int start = Math.max(0,messages.size() - (page + 1) * size);
        //int end = Math.min(messages.size(), start * size);
        int end = messages.size();
        List<GroupMessage> pagedMessages = messages.subList(start, end);

        List<GroupMessageDTO> structuredMessages = pagedMessages.stream()
                .map(message -> {
                    User sender = userRepository.findById(message.getSenderId())
                            .orElseThrow(() -> new RuntimeException("user not found"));

                    return new GroupMessageDTO(message, sender);
                })
                .toList();
        return ResponseEntity.ok(structuredMessages);
    }

}