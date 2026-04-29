package com.example.ayan.Chat.Application.Service;

import com.example.ayan.Chat.Application.Entity.Group;
import com.example.ayan.Chat.Application.Entity.User;
import com.example.ayan.Chat.Application.Model.AddMembersRequest;
import com.example.ayan.Chat.Application.Model.GroupAdminDTO;
import com.example.ayan.Chat.Application.Model.GroupMemberDTO;
import com.example.ayan.Chat.Application.Repository.GroupMessageRepository;
import com.example.ayan.Chat.Application.Repository.GroupRepository;
import com.example.ayan.Chat.Application.Repository.UserCustomRepository;
import com.example.ayan.Chat.Application.Repository.UserRepository;
import com.example.ayan.Chat.Application.Utils.ObjectIdMapper;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Component
@RequiredArgsConstructor
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupMessageRepository groupMessageRepository;

    @Autowired
    private UserCustomRepository userCustomRepository;

    @Autowired
    private ObjectIdMapper objectIdMapper;


    //creating the group
    public Group createGroup(String name, ObjectId creatorId, List<ObjectId> userIds){

        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("creator not found"));


        Group group = new Group();
        group.setName(name);
        group.setCreatedBy(creatorId);

        Set<ObjectId> members = new HashSet<>(userIds);
        members.add(creatorId); //as the creator will also be a member of that group
        group.setMembers(members);

        Set<ObjectId> admins = new HashSet<>();
        admins.add(creatorId);
        group.setAdmins(admins);

        groupRepository.save(group);

        userRepository.findAllById(userIds).forEach(user -> {
            if(user.getGroupChatList() == null){
                user.setGroupChatList(new HashSet<>());
            }
            if(creator.getGroupChatList() == null){
                creator.setGroupChatList(new HashSet<>());
            }
            user.getGroupChatList().add(group.getId());
            creator.getGroupChatList().add(group.getId());
            userRepository.save(user);
            userRepository.save(creator);
        });


        return group;
    }


    public Set<ObjectId> getGroup(String userId){

        User user = userRepository.findById(new ObjectId(userId))
                .orElseThrow(() -> new RuntimeException("user not found"));

       Set<ObjectId> groups = user.getGroupChatList();

        //groups.size(Comparator.comparing(Group::getCreatedAt).reversed());

        return groups;
    }


    //get the participants of the group.
    public List<GroupMemberDTO> getGroupMembers(String groupId){
        Group group = groupRepository.findById(new ObjectId(groupId))
                .orElseThrow(() -> new RuntimeException("group not found"));

        Set<ObjectId> groupMembers = group.getMembers();

        if(groupMembers == null || groupMembers.isEmpty()){
            throw new RuntimeException("members did not fetched");
        }

        List<User> memberUser = userRepository.findAllById(groupMembers);

        return memberUser.stream()
                .map(user -> GroupMemberDTO.builder()
                        .userId(user.getId().toHexString())
                        .userName(user.getUserName())
                        .build())
                .toList();
    }

    //fetch admins of the group
    public List<GroupAdminDTO> fetchAdmin(String groupId){
        Group group = groupRepository.findById(new ObjectId(groupId))
                .orElseThrow(() -> new RuntimeException("group not found"));

        Set<ObjectId> groupAdmins = group.getAdmins();

        if(groupAdmins == null || groupAdmins.isEmpty()){
           return List.of();
        }

        List<User> adminUsers = userRepository.findAllById(groupAdmins);

        return adminUsers.stream()
                .map(user -> GroupAdminDTO.builder()
                        .userId(user.getId().toHexString())
                        .userName(user.getUserName())
                        .build())
                .toList();
    }

    //edit group name.
    public Group editGroupName(String groupName, String groupId){

        Group group = groupRepository.findById(new ObjectId(groupId))
                .orElseThrow(() -> new RuntimeException("group not found"));

        if(group != null){
            group.setName(groupName);
            groupRepository.save(group);
        }

        return group;
    }


    //leave group
    public Group leaveGroup(String groupId, String userId){

        if(!ObjectId.isValid(groupId) || !ObjectId.isValid(userId)){
            throw new IllegalArgumentException("user or group id is invalid");
        }

        ObjectId groupObjectId = new ObjectId(groupId);
        ObjectId userObjectId = new ObjectId(userId);

        Group group = groupRepository.findById(groupObjectId)
                .orElseThrow(() -> new RuntimeException("group not found"));

        User user = userRepository.findById(userObjectId)
                .orElseThrow(() -> new RuntimeException("user not found"));

        if(!group.getMembers().contains(userObjectId)){
            throw new RuntimeException("user is not member of the group");
        }
        group.getMembers().remove(userObjectId);
        //group.getAdmins().remove(userObjectId);

        user.getGroupChatList().remove(groupObjectId);

        if(group.getMembers().isEmpty()){
            groupRepository.delete(group);
            return null;
        }

        return groupRepository.save(group);
    }


    //rejoin the group by member
    public Group rejoinGroup(String groupId, String userId){

        if(!ObjectId.isValid(groupId) || !ObjectId.isValid(userId)){
            throw new IllegalArgumentException("user or group id is not valid");
        }

        ObjectId gId = new ObjectId(groupId);
        ObjectId uId = new ObjectId(userId);

        User user = userRepository.findById(uId)
                .orElseThrow(() -> new RuntimeException("user not found"));

        Group group = groupRepository.findById(gId)
                .orElseThrow(() -> new RuntimeException("group not found"));

        if(group.getMembers().contains(uId)){
            throw new RuntimeException("user is already part of the group");
        }

        group.getMembers().add(uId);

        return groupRepository.save(group);
    }



    //adding the group members
    public Group addMembersToGroup(String groupId, String adminId, List<String> newMembers) {

        ObjectId groupObjId = new ObjectId(groupId);
        ObjectId adminObjId = new ObjectId(adminId);
        List<ObjectId> memberObjId = objectIdMapper.mapToObjectIdList(newMembers);

        User user = userRepository.findById(adminObjId)
                .orElseThrow(() -> new RuntimeException("admin not found"));

        Group group = groupRepository.findById(groupObjId)
                .orElseThrow(() -> new RuntimeException("group not found"));

        Set<ObjectId> currMember = group.getMembers();

        List<ObjectId> membersToAdd = memberObjId.stream()
                .filter(id -> !currMember.contains(id))
                .toList();

        if(membersToAdd.isEmpty()){
            throw new RuntimeException("all users already exist in the group");
        }

        if (!group.getAdmins().contains(adminObjId)) {
            throw new RuntimeException("only admin can add members");
        }

        currMember.addAll(membersToAdd);
        user.getGroupChatList().add(groupObjId);
        groupRepository.save(group);
        userRepository.save(user);

        return group;
    }

    //remove member from the group
    public Group removeMemberFromGroup(String groupId, String userId, String adminId){

        ObjectId groupObjId = new ObjectId(groupId);
        ObjectId userObjId = new ObjectId(userId);
        ObjectId adminObjId = new ObjectId(adminId);

        Group group = groupRepository.findById(groupObjId)
                .orElseThrow(() -> new RuntimeException("group not found"));

        User user = userRepository.findById(userObjId)
                .orElseThrow(() -> new RuntimeException("user not found"));

        if(!group.getMembers().contains(userObjId)){
            throw new RuntimeException("user is not part of the group");
        }

        if(!group.getAdmins().contains(adminObjId)){
            throw new RuntimeException("only admin can remove the user");
        }

        group.getMembers().remove(userObjId);
        user.getGroupChatList().remove(groupObjId);
        userRepository.save(user);
        groupRepository.save(group);

        return group;
    }


    //assigning normal users as admin of the group
    public Group assignAdmin(String groupId, String userId, String adminId){

        ObjectId groupObjId = new ObjectId(groupId);
        ObjectId adminObjId = new ObjectId(adminId);
        ObjectId userObjId = new ObjectId(userId);

        Group group = groupRepository.findById(groupObjId)
                .orElseThrow(() -> new RuntimeException("group not found"));

        if(!group.getAdmins().contains(adminObjId)){
            throw new RuntimeException("only admins can assign admins");
        }

        if(!group.getMembers().contains(userObjId)){
            throw new RuntimeException("to become admin users has to part of the group");
        }

        group.getAdmins().add(userObjId);
        return groupRepository.save(group);
    }

    //delete group by users
    public void deleteGroupByUser(String groupId, String userId){

        ObjectId gId = new ObjectId(groupId);
        ObjectId uId = new ObjectId(userId);

        Group group = groupRepository.findById(gId)
                .orElseThrow(() -> new RuntimeException("group not found"));

        User user = userRepository.findById(uId)
                .orElseThrow(() -> new RuntimeException("user not found"));

        group.getMembers().remove(uId);
        group.getAdmins().remove(uId);
        groupRepository.save(group);

        user.getGroupChatList().remove(gId);
        userRepository.save(user);

        groupMessageRepository.deleteByGroupIdAndSenderId(gId, uId);
    }

    //delete group by admin
    public void deleteGroupByAdmin(String groupId, String adminId){

        ObjectId gId = new ObjectId(groupId);
        ObjectId aId = new ObjectId(adminId);

        Group group = groupRepository.findById(gId)
                .orElseThrow(() -> new RuntimeException("group not found"));

        User user = userRepository.findById(aId)
                .orElseThrow(() -> new RuntimeException("user not found"));

        if(!group.getAdmins().contains(aId)){
            throw new RuntimeException("only admin can delete the group");
        }

        userCustomRepository.removeGroupFromAllUsers(gId);
        groupMessageRepository.deleteByGroupId(gId);
        groupRepository.delete(group);
    }


    //getting group id to check further if the group exists
    public Group getGroupById(ObjectId groupId){
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("group not found"));
    }


    //get the groups where a particular user is the member
    public List<Group> getUsersGroup(ObjectId userId){
        return groupRepository.findMembersContainById(userId);
    }


    public Group getGroupByName(String name){
        return groupRepository.findByName(name);
    }


}
