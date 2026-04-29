package com.example.ayan.Chat.Application.Config;

import com.example.ayan.Chat.Application.Service.JwtService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.security.Principal;
import java.util.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private JwtService jwtService;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat", "/group-message")
                .setAllowedOriginPatterns("http://localhost:5173")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {

            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                if(StompCommand.CONNECT.equals(accessor.getCommand())){
                    List<String> auth = accessor.getNativeHeader("Authorization");
                    if(auth != null && !auth.isEmpty()){
                        String raw = auth.get(0);
                        String token = raw.startsWith("Bearer ") ? raw.substring(7) : raw;
                        try{
                            ObjectId userId = jwtService.extractUserId("Bearer " + token);
                            //Principal principal = new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
                            Principal principal = userId::toHexString;
                            accessor.setUser(principal);
                            System.out.println("WebSocket CONNECT authenticated userId = " + userId);
                        }catch(Exception e){
                            System.out.println("Invalid websocket token: " + e.getMessage());
                        }
                    }
                }

                return message;
            }
        });
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue"); //broadcast to subscribers
        config.setApplicationDestinationPrefixes("/app"); //clients sends here

        config.setUserDestinationPrefix("/user");
    }
}
