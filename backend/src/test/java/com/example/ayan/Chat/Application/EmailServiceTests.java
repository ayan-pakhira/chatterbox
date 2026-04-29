package com.example.ayan.Chat.Application;

import com.example.ayan.Chat.Application.Service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmailServiceTests {


    @Autowired
    private EmailService emailService;

    @Test
    void testSendMail(){
        emailService.sendEmail(
                "ayan2k1709@gmail.com",
                "testing email sending with java",
                "yess!! it is working");
    }
}
