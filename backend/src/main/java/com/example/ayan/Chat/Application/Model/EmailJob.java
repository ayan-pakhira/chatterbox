package com.example.ayan.Chat.Application.Model;
import lombok.*;

import java.util.*;

@Data
@Getter
@Setter
@NoArgsConstructor
public class EmailJob {

    private String email;
    private String subject;
    private String body;

    public EmailJob(String email, String subject, String body){
        this.email = email;
        this.subject = subject;
        this.body = body;
    }
}
