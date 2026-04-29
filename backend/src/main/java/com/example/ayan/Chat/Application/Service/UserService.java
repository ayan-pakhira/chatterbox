package com.example.ayan.Chat.Application.Service;


import org.springframework.data.domain.Page;
import com.example.ayan.Chat.Application.Entity.PasswordResetToken;
import com.example.ayan.Chat.Application.Entity.User;
import com.example.ayan.Chat.Application.Model.RegisterRequest;
import com.example.ayan.Chat.Application.Model.SearchApiDTO;
import com.example.ayan.Chat.Application.Model.UserDTO;
import com.example.ayan.Chat.Application.Repository.PasswordResetTokenRepository;
import com.example.ayan.Chat.Application.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import java.rmi.registry.Registry;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    //to save the normal users
    //for user controller
    public User saveUserEntry(User user){

        user.setUserName(user.getUserName());
        user.setPassword(encoder.encode(user.getPassword()));
        user.setEmail(user.getEmail());
        user.setRoles(List.of("USER"));
        return userRepository.save(user);

    }

    //to save the admin entry and admin users
    //for admin controller
    public User saveAdminEntry(String userName, String password, String email){

        User admin = new User();

        admin.setUserName(userName);
        admin.setPassword(encoder.encode(password));
        admin.setEmail(email);
        admin.setRoles(Arrays.asList("ADMIN", "USER"));
        return userRepository.save(admin);
    }

    //to get the all users by admin
    //for admin controller
    public List<User> getAll(){
        return userRepository.findAll();
    }

    //search the users and fetch them if they are registered
   public Page<SearchApiDTO> searchUsers(String query, Pageable pageable){
        Page<User> users = userRepository.findByUserNameContainingIgnoreCase(query, pageable);

        return users.map(user -> new SearchApiDTO(
                user.getId(),
                user.getUserName(),
                user.getEmail()
        ));

   }



    public boolean deleteAll(){
        userRepository.deleteAll();
        return true;
    }


    //code for resetting the password and set new password
    public void resetPassword(String email, String otp, String newPassword){
        PasswordResetToken token = tokenRepository.findByEmail(email);

        if(token == null){
            throw new RuntimeException("OTP not found for this email");
        }

        if(!token.getOtp().equals(otp)){
            throw new RuntimeException("Invalid OTP");
        }

        if(token.getExpiryTime().isBefore(LocalDateTime.now())){
            throw new RuntimeException("OTP has expired");
        }

        User user = userRepository.findByEmail(email);

        if(user == null){
            throw new RuntimeException("User not found");
        }

        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);

        tokenRepository.delete(token);

    }




}
