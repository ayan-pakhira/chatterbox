package com.example.ayan.Chat.Application.Model;
import lombok.*;

import java.util.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddMembersRequest {


    private List<String> newMembers;


}
