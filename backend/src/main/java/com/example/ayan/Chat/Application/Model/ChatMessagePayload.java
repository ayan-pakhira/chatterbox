package com.example.ayan.Chat.Application.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.*;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessagePayload {

    private ObjectId groupId;
    private ObjectId senderId;
    private String content;
}
