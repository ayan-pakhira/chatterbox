package com.example.ayan.Chat.Application.Model;

import com.example.ayan.Chat.Application.Entity.Group;
import com.example.ayan.Chat.Application.Entity.GroupMessage;
import com.example.ayan.Chat.Application.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupMessageDTO {

    private String senderUserName;
    private String groupId;
    private String content;
    private Date timeStamp;


    public GroupMessageDTO(GroupMessage message, User sender){
        this.senderUserName = sender.getUserName();
        this.groupId = message.getGroupId().toHexString();
        this.content = message.getContent();
        this.timeStamp = message.getTimeStamp();
    }

}
