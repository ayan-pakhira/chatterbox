package com.example.ayan.Chat.Application.Model;

import co.elastic.clients.elasticsearch.core.search.Collector;
import com.example.ayan.Chat.Application.Entity.Group;
import com.example.ayan.Chat.Application.Entity.GroupMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.*;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupDTO {

    private String id;
    private String groupName;
    private String admin;
    private String lastMessage;
    private Date updatedAt;
    private Set<String> participants;



    public GroupDTO(Group group){
        this.id = group.getId().toHexString();
        this.groupName = group.getName();
        this.admin = group.getAdmins().toString();
        this.lastMessage = group.getLastMessage();
        this.updatedAt = group.getCreatedAt();
        this.participants = group.getMembers()
                .stream()
                .map(ObjectId::toHexString)
                .collect(Collectors.toSet());
    }



}
