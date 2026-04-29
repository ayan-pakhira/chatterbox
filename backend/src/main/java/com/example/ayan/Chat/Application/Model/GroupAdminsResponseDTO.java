package com.example.ayan.Chat.Application.Model;
import com.example.ayan.Chat.Application.Entity.Group;
import com.example.ayan.Chat.Application.Entity.User;
import lombok.*;
import org.bson.types.ObjectId;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class GroupAdminsResponseDTO {

    private String groupId;
    private List<GroupAdminDTO> groupAdmins;



}
