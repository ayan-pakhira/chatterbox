package com.example.ayan.Chat.Application.Controller;

import com.example.ayan.Chat.Application.Entity.Group;
import com.example.ayan.Chat.Application.Entity.User;
import com.example.ayan.Chat.Application.Model.*;
import com.example.ayan.Chat.Application.Repository.GroupRepository;
import com.example.ayan.Chat.Application.Repository.UserRepository;
import com.example.ayan.Chat.Application.Service.GroupService;
import com.example.ayan.Chat.Application.Service.JwtService;
import com.example.ayan.Chat.Application.Service.UserService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/group/api")
@CrossOrigin("http://localhost:5173")
@Builder
public class GroupController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupService groupService;

    @Autowired
    private JwtService jwtService;


    //endpoints for creating the group
    @PostMapping("/create-group")
    public ResponseEntity<?> createGroupOfUsers(@RequestHeader("Authorization") String authHeader,
                                                @RequestBody GroupRequest request){
        ObjectId creatorId = jwtService.extractUserId(authHeader);


        List<ObjectId> memberIds = request.getUserIds().stream()
                .map(ObjectId::new)
                .toList();

        Group group = groupService.createGroup(request.getGroupName(), creatorId, memberIds);


        GroupDTO groupChats = new GroupDTO(group);
        return ResponseEntity.ok(groupChats);
    }

    //get groups
    @GetMapping("/get-group-chat")
    public ResponseEntity<?> getGroupsOfUsers(@RequestHeader("Authorization") String authHeader){

        String senderId = String.valueOf(jwtService.extractUserId(authHeader));

        Set<ObjectId> groupIds = groupService.getGroup(senderId);

       List<Group> groups = groupRepository.findAllById(groupIds);


       List<GroupDTO> groupChats = groups.stream()
               .map(GroupDTO::new).toList();

       return ResponseEntity.ok(groupChats);
    }


    //--TODO-- to be tested
    // remarks - working
    //get group members
    @GetMapping("/group-members/{groupId}")
    public ResponseEntity<?> getGroupMembers(@PathVariable String groupId,
                                             @RequestHeader("Authorization") String authHeader){
        Group group = groupRepository.findById(new ObjectId(groupId))
                .orElseThrow(() -> new RuntimeException("group not found"));

        String userId = String.valueOf(jwtService.extractUserId(authHeader));

        User user = userRepository.findById(new ObjectId(userId))
                .orElseThrow(() -> new RuntimeException("user not found"));

        List<GroupMemberDTO> members = groupService.getGroupMembers(groupId);

        GroupMemberResponseDTO response = GroupMemberResponseDTO.builder()
                .groupId(groupId)
                .members(members)
                .build();

        return ResponseEntity.ok(response);
    }


    //--TODO - to be tested
    // remarks - working
    //get group admins
    @GetMapping("/group-admins/{groupId}")
    public ResponseEntity<?> getGroupAdmins(@PathVariable String groupId,
                                            @RequestHeader("Authorization") String authHeader){

        Group group = groupRepository.findById(new ObjectId(groupId))
                .orElseThrow(() -> new RuntimeException("group not found"));

        String userId = String.valueOf(jwtService.extractUserId(authHeader));

        User user = userRepository.findById(new ObjectId(userId))
                .orElseThrow(() -> new RuntimeException("user not found"));


        List<GroupAdminDTO> admins = groupService.fetchAdmin(groupId);

        GroupAdminsResponseDTO response = GroupAdminsResponseDTO.builder()
                .groupId(groupId)
                .groupAdmins(admins)
                .build();

        return ResponseEntity.ok(response);
    }

    //--TODO - to be tested
    //remarks - working
    //edit group name.
    @PutMapping("/edit-group-name/{groupId}")
    public ResponseEntity<?> editGroupName(@PathVariable String groupId,
                                           @RequestHeader("Authorization") String authHeader,
                                           @RequestBody EditGroupNameRequestDTO request){

        String userId = String.valueOf(jwtService.extractUserId(authHeader));

        Group group = groupRepository.findById(new ObjectId(groupId))
                .orElseThrow(() -> new RuntimeException("group not found"));

        User user = userRepository.findById(new ObjectId(userId))
                .orElseThrow(() -> new RuntimeException("user not found"));

        Group newGroupName = groupService.editGroupName(request.getName(), groupId);

        return ResponseEntity.ok(newGroupName);
    }


    // todo - to be tested
    //delete a group for individual user
    @DeleteMapping("/delete-group/{groupId}")
    public ResponseEntity<?> deleteGroup(@RequestHeader("Authorization") String authHeader,
                                         @PathVariable String groupId){

        String userId = String.valueOf(jwtService.extractUserId(authHeader));

        groupService.deleteGroupByUser(groupId, userId);

        return ResponseEntity.ok("deleted successfully");
    }

    // todo - to be tested
    //delete a group by admin
    @DeleteMapping("/delete-group-by-admin/{groupId}")
    public ResponseEntity<?> deleteGroupByAdmin(@RequestHeader("Authorization") String authHeader,
                                                @PathVariable String groupId){
        String userId = String.valueOf(jwtService.extractUserId(authHeader));

        User user = userRepository.findById(new ObjectId(userId))
                .orElseThrow(() -> new RuntimeException("user not found"));

        Group group = groupRepository.findById(new ObjectId(groupId))
                .orElseThrow(() -> new RuntimeException("group not found"));

        if(!group.getAdmins().contains(new ObjectId(userId))){
            throw new RuntimeException("only admin can delete the group");
        }

        groupService.deleteGroupByAdmin(groupId, userId);

        return ResponseEntity.ok("group deleted");
    }

    // todo - to be tested
    //leave group
    @PutMapping("/leave-group/{groupId}")
    public ResponseEntity<?> exitGroup(@RequestHeader("Authorization") String authHeader,
                                       @PathVariable String groupId){

        String userId = String.valueOf(jwtService.extractUserId(authHeader));

        Group newGroup = groupService.leaveGroup(userId, groupId);

        return ResponseEntity.ok(newGroup);
    }

    //rejoin the group
    @PutMapping("/rejoin-group/{groupId}")
    public ResponseEntity<?> rejoinGroupByUser(@RequestHeader("Authorization") String authHeader,
                                               @PathVariable String groupId){

        String userId = String.valueOf(jwtService.extractUserId(authHeader));

        Group group = groupService.rejoinGroup(groupId, userId);

        return ResponseEntity.ok(group);
    }


    // todo - to be tested
    //endpoints for adding members to the group.
    @PutMapping("/add-member/{groupId}")
    public ResponseEntity<?> addMembers(@PathVariable String groupId,
                                        @RequestHeader("Authorization") String authHeader,
                                        @RequestBody AddMembersRequest request){
        String adminId = String.valueOf(jwtService.extractUserId(authHeader));

        Group newGroup = groupService.addMembersToGroup(groupId, adminId, request.getNewMembers());
        return ResponseEntity.ok(newGroup);
    }

    // todo - to be tested
    //remove member from the group
    @PutMapping("/remove-member/{groupId}")
    public ResponseEntity<?> removeMember(@PathVariable String groupId,
                                          @RequestHeader("Authorization") String authHeader,
                                          @RequestBody GroupMemberDTO request){

        String adminId = String.valueOf(jwtService.extractUserId(authHeader));

        Group newGroup = groupService.removeMemberFromGroup(groupId, request.getUserId(), adminId);
        return ResponseEntity.ok(newGroup);
    }

    // todo - to be tested
    //assigning normal members as admin
    @PutMapping("/assign-admin/{groupId}")
    public ResponseEntity<?> assignAdmin(@PathVariable String groupId,
                                         @RequestBody GroupMemberDTO request,
                                         @RequestHeader("Authorization") String authHeader){
        String adminId = String.valueOf(jwtService.extractUserId(authHeader));
        Group newGroup = groupService.assignAdmin(groupId, request.getUserId(), adminId);
        return ResponseEntity.ok(newGroup);
    }


    //get group id
    @GetMapping("/{groupId}")
    public ResponseEntity<?> getGroup(@PathVariable ObjectId groupId){
        Group group = groupService.getGroupById(groupId);

        return ResponseEntity.ok(group);

    }

    //find a user attached to numbers of group
    @GetMapping("/my-groups")
    public ResponseEntity<?> getMyGroups(@RequestHeader("Authorization") String authHeader){
        ObjectId userIds = jwtService.extractUserId(authHeader);
        return ResponseEntity.ok(groupService.getUsersGroup(userIds));
    }


    //get the group by name
    @GetMapping("/get-group-by-name/{name}")
    public ResponseEntity<?> groupByName(@PathVariable String name){
        Group group = groupService.getGroupByName(name);

        GroupDTO dto = new GroupDTO(group);
        return ResponseEntity.ok(dto);
    }
}
