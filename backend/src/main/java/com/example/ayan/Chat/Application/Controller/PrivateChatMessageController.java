package com.example.ayan.Chat.Application.Controller;

import com.example.ayan.Chat.Application.Entity.PrivateChatMessage;
import com.example.ayan.Chat.Application.Entity.PrivateConversation;
import com.example.ayan.Chat.Application.Entity.User;
import com.example.ayan.Chat.Application.Repository.PrivateChatMessageRepository;
import com.example.ayan.Chat.Application.Repository.PrivateConversationRepository;
import com.example.ayan.Chat.Application.Repository.UserRepository;
import com.example.ayan.Chat.Application.Service.JwtService;
import com.example.ayan.Chat.Application.Service.PrivateChatMessageService;
import com.example.ayan.Chat.Application.Service.PrivateConversationService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat")
public class PrivateChatMessageController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PrivateChatMessageService privateChatMessageService;

    @Autowired
    private PrivateChatMessageRepository privateChatMessageRepository;

    @Autowired
    private PrivateConversationRepository privateConversationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private PrivateConversationService privateConversationService;


    @PostMapping("/message")
    public ResponseEntity<?> createMessage(@RequestHeader("Authorization") String authHeader,
                                           @RequestBody Map<String, String> body) {

        // 1. Extract senderId from JWT
        ObjectId senderId = jwtService.extractUserId(authHeader);

        // 2. Extract chatId and content from request
        String chatIdStr = body.get("chatId");
        String content = body.get("content");

        if (chatIdStr == null || chatIdStr.isBlank()) {
            return ResponseEntity.badRequest().body("chatId is required");
        }

        ObjectId chatId = new ObjectId(chatIdStr);

        // 3. Fetch the conversation from DB
        PrivateConversation conversation = privateConversationRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found with this id"));

        // 4. Validate: sender must be part of this chat
        if (!conversation.getParticipants().contains(senderId.toHexString())) {
            throw new RuntimeException("You are not a participant of this private chat");
        }

        // 5. Create the message
        PrivateChatMessage message = new PrivateChatMessage();
        message.setSenderId(senderId.toHexString());
        message.setChatId(chatId.toHexString());
        message.setContent(content);

        // 6. Save the message
        PrivateChatMessage savedMessage = privateChatMessageService.saveMessages(message);

        // 7. WebSocket broadcast to both users (like group)
        simpMessagingTemplate.convertAndSend("/topic/private." + chatId.toHexString(), savedMessage);

        // 8. Send response
        return ResponseEntity.ok(savedMessage);
    }



    @MessageMapping("/private.sendMessage")
    public void sendPrivateMessage(PrivateChatMessage message, Principal principal) {

        if (principal == null) {
            throw new RuntimeException("Unauthorized STOMP message");
        }

        // Set sender ID from Principal
        String senderId = principal.getName();
        message.setSenderId(senderId);

        // Validate chat
        if (message.getChatId() == null || message.getChatId().isEmpty()) {
            throw new RuntimeException("chat id is missing in STOMP message");
        }

        PrivateConversation conversation = privateConversationRepository.findById(new ObjectId(message.getChatId()))
                .orElseThrow(() -> new RuntimeException("chat not found"));

        if(!conversation.getParticipants().contains(senderId)){
            throw new RuntimeException("sender is not part of the chat");
        }

        // Save message in DB
        PrivateChatMessage savedMessage = privateChatMessageService.saveMessages(message);


        // Send message to receiver's topic
        for(String participantId : conversation.getParticipants()){
            if(!participantId.equals(senderId)){
                simpMessagingTemplate.convertAndSend("/topic/private." + participantId, savedMessage);
            }
        }

    }


    //fetching the messages from the database
    @GetMapping("/get-messages/{chatId}")
    public ResponseEntity<?> getConversation(@RequestHeader("Authorization") String authHeader,
                                             @PathVariable String chatId){

        String senderId = String.valueOf(jwtService.extractUserId(authHeader));

        List<PrivateChatMessage> messages = privateChatMessageService.getMessages(chatId);

        List<Map<String, Object>> response = messages.stream().map(msg -> Map.<String, Object>of(
                "id", msg.getId(),
                "senderId", msg.getSenderId(),
                "chatId", msg.getChatId(),
                "content", msg.getContent(),
                "createdAt", msg.getTimeStamp()

        )).toList();

        return ResponseEntity.ok(response);
    }


}





















