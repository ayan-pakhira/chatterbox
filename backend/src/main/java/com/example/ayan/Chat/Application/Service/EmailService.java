package com.example.ayan.Chat.Application.Service;

import com.example.ayan.Chat.Application.Model.EmailAuth;
import com.example.ayan.Chat.Application.Repository.EmailAuthRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private EmailAuthRepository emailAuthRepository;

    public void sendEmail(String toUser, String body, String subject){
        boolean isSent = false;

        try{

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toUser);
            message.setSubject(subject);
            message.setText(body);

            javaMailSender.send(message);
            isSent = true;

        }catch(Exception e){
            throw new RuntimeException("Error in sending email: " + e.getMessage());
        }

        // emailAuth = new EmailAuth(toUser, body, subject, isSent, LocalDateTime.now());
        //emailAuthRepository.save(emailAuth);
    }


    public void sendMailOtp(String toEmail, String body){

        boolean isSent = false;

        try{

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setText(body);

            javaMailSender.send(message);
            isSent = true;

        }catch(Exception e){
            throw new RuntimeException("error in sending otp: " + e.getMessage());
        }

        EmailAuth emailAuth = new EmailAuth(toEmail, body, LocalDateTime.now());
        //emailAuthRepository.save(emailAuth);
    }
}
