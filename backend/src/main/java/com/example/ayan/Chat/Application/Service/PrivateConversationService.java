package com.example.ayan.Chat.Application.Service;
import com.example.ayan.Chat.Application.Entity.PrivateConversation;
import com.example.ayan.Chat.Application.Entity.User;
import com.example.ayan.Chat.Application.Repository.PrivateConversationRepository;
import com.example.ayan.Chat.Application.Repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PrivateConversationService {


    @Autowired
    private PrivateConversationRepository conversationRepository;

    @Autowired
    private UserRepository userRepository;


    //fetching the existing conversation or creating a new one
    public PrivateConversation getOrCreateConversation(String user1Id, String user2Id) {



        List<PrivateConversation> existingChats =
                conversationRepository.findByParticipantsPair(user1Id, user2Id);

        if (!existingChats.isEmpty()) {
            // If multiple found, just return the first one (you can later clean duplicates)
            return existingChats.getFirst();
        }


        PrivateConversation newConversation = new PrivateConversation();
        newConversation.setParticipants(List.of(user1Id, user2Id));
        newConversation.setLastMessage("");
        newConversation.setUpdatedAt(new Date());

        PrivateConversation savedConversation = conversationRepository.save(newConversation);

        User user1 = userRepository.findById(new ObjectId(user1Id))
                .orElseThrow(() -> new RuntimeException("user not found"));

        User user2 = userRepository.findById(new ObjectId(user2Id))
                .orElseThrow(() -> new RuntimeException("user not found"));

        if(!user1.getChatList().contains(savedConversation.getId())){
            user1.getChatList().add(savedConversation);
        }

        if(!user2.getChatList().contains(savedConversation.getId())){
            user2.getChatList().add(savedConversation);
        }

        userRepository.save(user1);
        userRepository.save(user2);

        return savedConversation;
    }

    //fetching all conversation for a user
    public List<PrivateConversation> getConversation(String userId){

        User user = userRepository.findById(new ObjectId(userId))
                .orElseThrow(() -> new RuntimeException("user not found"));

        List<PrivateConversation> chats = user.getChatList();

        chats.sort(Comparator.comparing(PrivateConversation::getUpdatedAt).reversed());

        return chats;
    }

    //rename the conversation
    public PrivateConversation editChatName(String chatName, String chatId){

        PrivateConversation conversation = conversationRepository.findById(new ObjectId(chatId))
                .orElseThrow(() -> new RuntimeException("chat not found"));

        return null;
    }



}
