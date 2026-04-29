package com.example.ayan.Chat.Application.Model;
import lombok.Data;

import java.util.*;

@Data
public class GroupRequest {

    private String groupName;
    List<String> userIds;
}
