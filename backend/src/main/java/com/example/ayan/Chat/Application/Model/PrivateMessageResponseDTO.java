package com.example.ayan.Chat.Application.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrivateMessageResponseDTO {

    private String senderId;
    private String receiverId;
    private String senderName;
    private String receiverName;
    private String content;
    private Date timeStamp;
}
