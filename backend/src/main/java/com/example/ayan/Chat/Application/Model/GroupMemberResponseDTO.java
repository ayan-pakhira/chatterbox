package com.example.ayan.Chat.Application.Model;
import lombok.*;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class GroupMemberResponseDTO {

    private String groupId;
    private List<GroupMemberDTO> members;

}
