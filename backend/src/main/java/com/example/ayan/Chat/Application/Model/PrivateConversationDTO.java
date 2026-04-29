package com.example.ayan.Chat.Application.Model;
import com.example.ayan.Chat.Application.Entity.PrivateConversation;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.util.*;

@Data
@Getter
@Setter
@NoArgsConstructor
public class PrivateConversationDTO {


    private String id;
    private String chatName;
    private String lastMessage;
    private Date updatedAt;
    private List<String> participants;

    public PrivateConversationDTO(PrivateConversation conversation){
        this.id = conversation.getId().toHexString();
        this.lastMessage = conversation.getLastMessage();
        this.updatedAt = conversation.getUpdatedAt();
        this.participants = conversation.getParticipants();

    }


}
