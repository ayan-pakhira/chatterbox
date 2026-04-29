package com.example.ayan.Chat.Application.Service;
import com.example.ayan.Chat.Application.Model.EmailJob;
import com.example.ayan.Chat.Application.Utils.RedisQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EmailQueueProducer {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    public void pushEmailJob(EmailJob job){
        redisTemplate.opsForList().leftPush(
                RedisQueue.Email_Queue,
                job
        );
    }

}
