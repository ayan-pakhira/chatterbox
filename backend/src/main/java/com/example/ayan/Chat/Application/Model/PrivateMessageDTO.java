package com.example.ayan.Chat.Application.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrivateMessageDTO {

    private String senderId;
    private String receiverId;
    private String content;

}
