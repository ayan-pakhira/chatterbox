package com.example.ayan.Chat.Application.Service;

import com.example.ayan.Chat.Application.Entity.GroupMessage;
import com.example.ayan.Chat.Application.Repository.GroupMessageRepository;
import com.example.ayan.Chat.Application.Repository.GroupRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Component
public class GroupMessageService {

    @Autowired
    private GroupMessageRepository groupMessageRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupService groupService;


    //for saving or persisting group message in database
    public GroupMessage saveMessage(GroupMessage message){
        message.setTimeStamp(new Date());
        return groupMessageRepository.save(message);
    }


    //fetching the group messages
    public List<GroupMessage> getMessage(ObjectId groupId){
        return groupMessageRepository.findByGroupIdOrderByTimeStampAsc(groupId);
    }


}
