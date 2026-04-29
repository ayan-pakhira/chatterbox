package com.example.ayan.Chat.Application.Service;
import com.example.ayan.Chat.Application.Entity.PasswordResetToken;
import com.example.ayan.Chat.Application.Repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PasswordResetTokenService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    public String generateOtp(String email){
        String otp = String.format("%06d", new Random().nextInt(999999));

        tokenRepository.deleteByEmail(email);

        PasswordResetToken token = new PasswordResetToken();
        token.setEmail(email);
        token.setOtp(otp);
        token.setExpiryTime(LocalDateTime.now().plusMinutes(10));

        tokenRepository.save(token);
        emailService.sendMailOtp(email, otp);

        return otp;
    }

    public void generateResetPasswordOtp(String email){
        String otp = String.format("%06d", new Random().nextInt(999999));
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(10);

        PasswordResetToken token = tokenRepository.findByEmail(email);

        if(!(token == null)){
            tokenRepository.deleteByEmail(email);
        }

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setEmail(email);
        resetToken.setOtp(otp);
        resetToken.setExpiryTime(expiryTime);

        tokenRepository.save(resetToken);
        emailService.sendMailOtp(email, "Password Reset OTP" + otp);

    }
}
