package com.example.ayan.Chat.Application.Controller;
import com.example.ayan.Chat.Application.Entity.PrivateConversation;
import com.example.ayan.Chat.Application.Entity.User;
import com.example.ayan.Chat.Application.Model.PrivateConversationDTO;
import com.example.ayan.Chat.Application.Repository.PrivateConversationRepository;
import com.example.ayan.Chat.Application.Repository.UserRepository;
import com.example.ayan.Chat.Application.Service.JwtService;
import com.example.ayan.Chat.Application.Service.PrivateConversationService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/private")
public class PrivateConversationController {

    @Autowired
    private PrivateConversationService privateConversationService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PrivateConversationRepository privateConversationRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/get-or-create-chat/{receiverId}")
    public ResponseEntity<?> getOrCreateNewChat(@RequestHeader("Authorization") String authHeader,
                                                @PathVariable String receiverId){

        String senderId = String.valueOf(jwtService.extractUserId(authHeader));
        User receiver = userRepository.findById(new ObjectId(receiverId))
                .orElseThrow(() -> new RuntimeException("user not found"));

        PrivateConversation conversation = privateConversationService.getOrCreateConversation(senderId, receiverId);

        PrivateConversationDTO conversationDTO = new PrivateConversationDTO();
        conversationDTO.setId(conversation.getId().toHexString());
        conversationDTO.setChatName(receiver.getUserName());
        conversationDTO.setLastMessage(conversation.getLastMessage());
        conversationDTO.setParticipants(conversation.getParticipants());
        conversationDTO.setUpdatedAt(conversation.getUpdatedAt());


        return ResponseEntity.ok(conversationDTO);
    }


    //fetch all conversation for users
    @GetMapping("/chat-list")
    public ResponseEntity<List<PrivateConversationDTO>> getConversationList(@RequestHeader("Authorization") String authHeader){

        String senderId = String.valueOf(jwtService.extractUserId(authHeader));

        List<PrivateConversation> conversations = privateConversationService.getConversation(senderId);

        List<PrivateConversationDTO> chatLists = conversations.stream().map(
                conversation -> {
                    PrivateConversationDTO dto = new PrivateConversationDTO(conversation);

                    String chatNameId = conversation.getParticipants()
                            .stream()
                            .filter(id -> !id.equals(senderId))
                            .findFirst()
                            .orElse(null);

                    if(chatNameId != null){
                       Optional< User> user = Optional.ofNullable(userRepository.findById(new ObjectId(chatNameId))
                               .orElseThrow(() -> new RuntimeException("user not found")));
                        user.ifPresent(value -> dto.setChatName(value.getUserName()));
                    }

                    return dto;
                }
        ).toList();

        return ResponseEntity.ok(chatLists);
    }


    // todo - to be tested
    // remarks - not working
    //delete a single chat
    @DeleteMapping("/delete-chat/{chatId}")
    public ResponseEntity<?> deleteSingleChat(@PathVariable String chatId,
                                              @RequestHeader("Authorization") String authHeader){

        String userId = String.valueOf(jwtService.extractUserId(authHeader));

        User user = userRepository.findById(new ObjectId(userId))
                .orElseThrow(() -> new RuntimeException("user not found"));

        PrivateConversation conversation = privateConversationRepository.findById(new ObjectId(chatId))
                .orElseThrow(() -> new RuntimeException("chat not found"));

        if(conversation != null){
            user.getGroupChatList().remove(new ObjectId(chatId));
            userRepository.save(user);
        }

        return ResponseEntity.ok("chat deleted");

    }


    //for testing purpose, deleting all the chat from the users list
    @DeleteMapping("/delete-chats-users")
    public ResponseEntity<?> deleteAllChatsFromUsers(@RequestHeader("Authorization") String authHeader){

        String senderId = String.valueOf(jwtService.extractUserId(authHeader));

        User user = userRepository.findById(new ObjectId(senderId))
                .orElseThrow(() -> new RuntimeException("user not found"));

        List<PrivateConversation> conversations = user.getChatList();

        privateConversationRepository.deleteAll(conversations);

        user.getChatList().removeAll(conversations);
        userRepository.save(user);

        return ResponseEntity.ok("deleted successfully");

    }



}
