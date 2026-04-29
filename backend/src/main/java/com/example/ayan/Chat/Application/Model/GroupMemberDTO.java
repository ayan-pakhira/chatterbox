package com.example.ayan.Chat.Application.Model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupMemberDTO {

    private String userId;
    private String userName;
}
