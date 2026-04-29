package com.example.ayan.Chat.Application.Controller;

import com.example.ayan.Chat.Application.Entity.PrivateChatMessage;
import com.example.ayan.Chat.Application.Entity.RefreshToken;
import com.example.ayan.Chat.Application.Entity.User;
import com.example.ayan.Chat.Application.Model.*;
import com.example.ayan.Chat.Application.Repository.*;
import com.example.ayan.Chat.Application.Service.*;
import com.example.ayan.Chat.Application.Utils.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder.encode;

@RestController
@RequestMapping("/public")
@CrossOrigin("http://localhost:5173")
@Slf4j
public class PublicController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager auth;


    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshTokenService tokenService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailAuthRepository emailAuthRepository;

    @Autowired
    private PasswordResetTokenService resetTokenService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PrivateChatMessageRepository messageRepository;

    @Autowired
    private PrivateConversationRepository privateConversationRepository;

    @Autowired
    private EmailQueueProducer emailQueueProducer;


    //endpoints for generating access token from refresh token
    @PostMapping("/generate-access-token")
    public ResponseEntity<?>refreshAccessToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ){

        if(refreshToken == null || refreshToken.isEmpty()){
            throw new RuntimeException("refresh token is required");
        }

        String newAccessToken = tokenService.generateNewAccessToken(refreshToken);
        Map<String, String> response = new HashMap<>();
        response.put("accessToken", newAccessToken);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register-user")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request,
                                          HttpServletResponse response){


        try{

            User user = new User();
            user.setUserName(request.getUserName());
            user.setEmail(request.getEmail());
            user.setPassword(request.getPassword());

            User savedUser = userService.saveUserEntry(user);

            auth.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getEmail(), request.getPassword()
            ));

            String accessToken = jwtUtil.generateToken(request.getEmail());
            RefreshToken refreshToken = tokenService.createToken(savedUser.getEmail());

            ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken.getRefreshToken())
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60)
                    .sameSite("Lax")
                    .build();


            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            RegisterResponseDTO res = new RegisterResponseDTO(
                    user.getId(),
                    request.getEmail(),
                    request.getUserName(),
                    accessToken


            );

            EmailJob job = new EmailJob(
                    request.getEmail(),
                    "congratulations!! you have successfully registered",
                    "welcome to chat app"
            );



            //emailService.sendMailOtp(request.getEmail(), "Congratulations!!! You have Successfully Registered!!");

            emailQueueProducer.pushEmailJob(job);

            return new ResponseEntity<>(res, HttpStatus.OK);

        }catch(Exception e){
            throw new RuntimeException("error while registering the user");
        }
    }



    @PostMapping("/api/login")
    public ResponseEntity<?> loginUser(@RequestBody UserDTO user,
                                       HttpServletResponse response){

        try{
            auth.authenticate(new UsernamePasswordAuthenticationToken
                    (user.getEmail(), user.getPassword()));

            User validateUser = userRepository.findByEmail(user.getEmail());
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
            String accessToken = jwtUtil.generateToken(userDetails.getUsername());
            RefreshToken refreshToken = tokenService.createToken(user.getEmail());

            ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken.getRefreshToken())
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60)
                    .sameSite("Lax")
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            LoginResponseDTO loginResponse = new LoginResponseDTO(
                    validateUser.getId(),
                    validateUser.getEmail(),
                    accessToken,
                    validateUser.getUserName()
            );

            return ResponseEntity.ok(loginResponse);

        } catch (Exception e) {
            log.error("Exception occurred while createAuthenticationToken ", e);
            return new ResponseEntity<>("incorrect username or password", HttpStatus.BAD_REQUEST);
        }
    }


    //logout for the user
    @PostMapping("/api/logout")
    public ResponseEntity<?> logoutUser(HttpServletResponse response){

        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok("logged out successfully");
    }

    //generate otp for verification purpose.
    @PostMapping("/generate-otp")
    public ResponseEntity<?> requestOtp(@RequestBody Map<String, String> body){

        String email = body.get("email");

        String otp = resetTokenService.generateOtp(email);

        return ResponseEntity.ok("otp sent");
    }


    //generate otp for password reset controller
    @PostMapping("/request-otp")
    public ResponseEntity<?> resetPasswordOTP(@RequestParam String email){
        resetTokenService.generateResetPasswordOtp(email);

        return ResponseEntity.ok("OTP generated");
    }



    //controller for resetting the old pwd and set a new pwd
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String email,
                                           @RequestParam String otp,
                                           @RequestParam String newPassword){
        userService.resetPassword(email, otp, newPassword);

        return ResponseEntity.ok("Password reset Successful");
    }




    //this is only for testing purpose to delete all the users one at a time.
    @DeleteMapping("/delete-users")
    public ResponseEntity<?> deleteAllUsers(){
        userService.deleteAll();
        return ResponseEntity.ok("Deleted all users");
    }


    //this is only for testing purpose, deleting all the refresh token altogether
    @DeleteMapping("/delete-tokens")
    public ResponseEntity<?> deleteAllTokens(){
        refreshTokenRepository.deleteAll();
        return ResponseEntity.ok("deleted all tokens");
    }


    //delete all messages, only for testing purpose
    @DeleteMapping("/delete-messages/{userId}")
    public ResponseEntity<?> deleteAllMessages(@PathVariable ObjectId userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("user not found"));

       List<PrivateChatMessage> messages = messageRepository.findAll().stream()
               .filter(msg -> msg.getSenderId().equals(userId))
               .toList();

       messageRepository.deleteAll(messages);

       user.getMessages().removeAll(messages);
       userRepository.save(user);



        return ResponseEntity.ok("deleted all messages");
    }


    //delete all conversation list
    @DeleteMapping("/delete-chats")
    public ResponseEntity<?> deleteAllChats(){
        privateConversationRepository.deleteAll();

        return ResponseEntity.ok("delete all chats");
    }



}
