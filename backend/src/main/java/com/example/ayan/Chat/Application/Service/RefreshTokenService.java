package com.example.ayan.Chat.Application.Service;
import com.example.ayan.Chat.Application.Entity.RefreshToken;
import com.example.ayan.Chat.Application.Entity.User;
import com.example.ayan.Chat.Application.Repository.RefreshTokenRepository;
import com.example.ayan.Chat.Application.Repository.UserRepository;
import com.example.ayan.Chat.Application.Utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;


    //generating the refresh token
    public RefreshToken createToken(String email){

        User user = userRepository.findByEmail(email);

        if(user == null){
            throw new RuntimeException("user not found");
        }

        RefreshToken token = RefreshToken.builder()
                .refreshToken(UUID.randomUUID().toString())
                .userId(String.valueOf(user))
                .createdAt(Instant.now())
                .expiry(Instant.now().plus(Duration.ofDays(7)))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(token);
    }


    //verifying the refresh token
    public RefreshToken verifyToken(String refreshToken){
        RefreshToken token = refreshTokenRepository.findById(refreshToken)
                .orElseThrow(() -> new RuntimeException("token not exists"));

        if(token.isRevoked()){
            throw new IllegalArgumentException("token has revoked");
        }

        if(token.getExpiry().isBefore(Instant.now())){
            throw new IllegalArgumentException("token expired");
        }

        return token;
    }


    //generating new access token
    public String generateNewAccessToken(String token){
        RefreshToken refreshToken = refreshTokenRepository.findById(token).
                orElseThrow(() -> new RuntimeException("Invalid Refresh Token"));

        if(refreshToken.isRevoked()){
            throw new RuntimeException("Refresh token is revoked");
        }

        if(refreshToken.getExpiry().isBefore(Instant.now())){
            throw new RuntimeException("Refresh token is expired");
        }

        User user = refreshToken.getUser();
        if(user == null){
            throw new RuntimeException("user not found with this token");
        }

        return jwtUtil.generateToken(user.getEmail());
    }

}

