package com.example.ayan.Chat.Application.Service;
import com.example.ayan.Chat.Application.Model.EmailJob;
import com.example.ayan.Chat.Application.Utils.RedisQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EmailQueueConsumer {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private EmailService emailService;

    @Scheduled(fixedDelay = 8000)
    public void processEmailQueue(){

        EmailJob job = (EmailJob) redisTemplate.opsForList()
                .rightPop(RedisQueue.Email_Queue);


        if(job != null){
            emailService.sendEmail(
                    job.getEmail(),
                    job.getSubject(),
                    job.getBody()
            );
        }

    }
}
