package com.example.ayan.Chat.Application.Model;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Data
@Getter
@Setter
@Builder
public class RegisterRequest {

    private String userName;
    private String email;
    private String password;
}
