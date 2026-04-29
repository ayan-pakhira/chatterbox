package com.example.ayan.Chat.Application.Controller;

import com.example.ayan.Chat.Application.Entity.User;
import com.example.ayan.Chat.Application.Model.UserDTO;
import com.example.ayan.Chat.Application.Repository.UserRepository;
import com.example.ayan.Chat.Application.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/admin/auth")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    //creating the admin
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(@RequestBody User request){
        User createdAdmin = userService.saveAdminEntry(request.getUserName(), request.getPassword(), request.getEmail());

        return ResponseEntity.ok("Admin has created");
    }

    //fetching all the user details
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/data")
    public ResponseEntity<List<User>> getAllUserDetails(){
        List<User> allUsers = userService.getAll();

        if(allUsers != null && !allUsers.isEmpty()){
            return new ResponseEntity<>(allUsers, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }







}
