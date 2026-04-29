package com.example.ayan.Chat.Application.Service;
import com.example.ayan.Chat.Application.Entity.PrivateChatMessage;
import com.example.ayan.Chat.Application.Entity.PrivateConversation;
import com.example.ayan.Chat.Application.Entity.User;
import com.example.ayan.Chat.Application.Model.PrivateMessageDTO;
import com.example.ayan.Chat.Application.Repository.PrivateChatMessageRepository;
import com.example.ayan.Chat.Application.Repository.PrivateConversationRepository;
import com.example.ayan.Chat.Application.Repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Service
@Component
public class PrivateChatMessageService {

    @Autowired
    private JwtService jwtService;


    @Autowired
    private PrivateChatMessageRepository privateMessageRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PrivateConversationRepository privateConversationRepository;


    public PrivateChatMessage saveMessages(PrivateChatMessage message) {

        // 1. Fetch sender
        User sender = userRepository.findById(new ObjectId(message.getSenderId()))
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        // 2. Fetch conversation using chatId
        PrivateConversation conversation = privateConversationRepository
                .findById(new ObjectId(message.getChatId()))
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        // 3. Set metadata
        message.setSenderName(sender.getUserName());
        message.setTimeStamp(new Date());

        // 4. Save message
        PrivateChatMessage savedMessage = privateMessageRepository.save(message);

        // 5. Add message to conversation
        conversation.getMessages().add(savedMessage);
        privateConversationRepository.save(conversation);

        return savedMessage;
    }


    public List<PrivateChatMessage> getMessages(String chatId){
       List<PrivateChatMessage> messages = privateMessageRepository.findConversation(chatId);

       messages.sort(Comparator.comparing(PrivateChatMessage::getTimeStamp));

       return messages;
    }
}
