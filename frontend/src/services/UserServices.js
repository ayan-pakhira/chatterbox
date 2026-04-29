import privateApi from "../services/PrivateApi.js";

export const getAllUsers = async (query, page = 0, size = 10) => {
    try{

        const response = await privateApi.get("/user/auth/api/search",{
            params: {q: query, page, size},
        });

        return response.data;

    }catch(error){
        console.log("failed to fetch the users, ", error)
        return {content: [], totalPages: 0}
    }
}

export const sendFriendRequest = async (receiverId) => {
    try{

        const response = await privateApi.post(`/friends/send-request/${receiverId}`);
        return response.data;

    }catch(error){
        console.log("error in sending friend request ", error)
        throw error;
    }
}

export const acceptFriendRequest = async (senderId) => {
    try{

        const response = await privateApi.post(`/friends/accept-request/${senderId}`);
        return response.data;

    }catch(error){
        console.log("error in accepting friend request ", error)
        throw error;
    }
}

export const rejectFriendRequest = async (senderId) => {
    try{

        const response = await privateApi.post(`/friends/reject-request/${senderId}`);
        return response.data;

    }catch(error){
        console.log("error in rejecting friend request ", error);
        throw error;
    }
}

export const getFriendRequests = async () => {
    try{

        const response = await privateApi.get('/friends/friend-requests');
        return response.data;

    }catch(error){
        console.log("error in fetching friend requests ", error);
        throw error;
    }
}

export const getFriendList = async () => {
    try{
        const response = await privateApi.get('/friends/friend-list');
        return response.data;

    }catch(error){
        console.log("error in fetching friend list ", error);
        throw error;
    }
}

export const openOrCreateChat = async (receiverId) => {

    try{

        const response = await privateApi.post(`/private/get-or-create-chat/${receiverId}`);
        return response.data;

    }catch(error){
        console.error("error in creating or opening a chat", error);
    }
}


export const fetchConversationList = async () => {
    try{

        const response = await privateApi.get('/private/chat-list');
        return response.data;

    }catch(error){
        console.log("error in fetching the chat list", error);
        throw error;
    }
}

export const fetchMessages = async (chatId) => {
    try{

        const response = await privateApi.get(`/chat/get-messages/${chatId}`,{
            headers: {
                "Content-Type": "application/json"
            }
        });
        return response.data;
    }catch(error){
        console.log("error in fetching messages ", error);
        throw error;
    }
}

export const fetchGroupChatLists = async () => {

    try{

        const response = await privateApi.get('/group/api/get-group-chat');
        return response.data;

    }catch(error){
        console.log("error in fetching the group chat list", error);
        throw error;
    }
}

export const createGroupChat = async (creatorId, groupName, memberIds) => {
    try{

        const response = await privateApi.post(`/group/api/create-group`,{
            groupName,
            userIds: memberIds,
        }, 
        {
            headers: {
                "Content-Type": "application/json"
            },
        });

        return response.data;

    }catch(error){
        console.log("error in creating group chat ", error);    
        throw error;
    }
}


export const fetchGroupMessages = async (groupId) => {

    try{

        const response = await privateApi.get(`/group-message/get-message/${groupId}`, {
            headers: {
                "Content-Type": "application/json"
            }
        })

        return response.data;

    }catch(error){
        console.log("error in fetching group messages ", error);
        throw error;
    }
}

export const fetchGroupMembers = async (groupId) => {

    try{
        const response = await privateApi.get(`/group/api/group-members/${groupId}`, {
            headers: {
                "Content-Type": "application/json"
            }
        })

        return response.data;

    }catch(error){
        console.log("error in fetching group members ", error);
        throw error;
    }
}

export const fetchGroupAdmins = async (groupId) => {

    try{
        const response = await privateApi.get(`/group/api/group-admins/${groupId}`, {
            headers: {
                "Content-Type": "application/json"
            }
        })

        return response.data;

    }catch(error){
        console.log("error in fetching group admins ", error);
        throw error;
    }
}

export const editGroupName = async (groupId, name) => {
    try{

        const response = await privateApi.put(`/group/api/edit-group-name/${groupId}`, {
            name
        }, {
            headers: {
                "Content-Type": "application/json"
            }
        });

        return response.data;

    }catch(error){
        console.log("error in editing group name ", error);
        throw error;
    }
}

export const deleteGroupByUser = async (groupId) => {
    try{
        const response = await privateApi.delete(`/group/api/delete-group/${groupId}`, {
            headers: {
                "Content-Type": "application/json"
            }
        });

        return response.data;

    }catch(error){
        console.log("error in deleting group ", error);
        throw error;
    }
}

export const deleteGroupByAdmin = async (groupId) => {
    try{
        const response = await privateApi.delete(`/group/api/delete-group-by-admin/${groupId}`, 
            {},
             {
            headers: {
                "Content-Type": "application/json"
            }   
        });

        return response.data;

    }catch(error){
        console.log("error in admin deleting group ", error);
        throw error;
    }
}

export const addMembersToGroup = async (groupId, newMembers) => {
    try{

        const response = await privateApi.put(`/group/api/add-member/${groupId}`, {
            newMembers
        }, {
            headers: {
                "Content-Type": "application/json"
            }
        });

        return response.data;

    }catch(error){
        console.log("error in adding members to group ", error);
        throw error;
    }
}

export const removeMemberFromGroup = async (groupId, userId) => {

    try{
        const response = await privateApi.put(`/group/api/remove-member/${groupId}`, {
            userId
        }, {
            headers: {
                "Content-Type": "application/json"
            }       
        })

        return response.data;

    }catch(error){
        console.log("error in removing member from group ", error);
        throw error;
    }
}

export const leaveGroupByUser = async (groupId) => {
    try{
        const response =await privateApi.put(`/group/api/leave-group/${groupId}`,
            {},
            {
            headers: {
                "Content-Type": "application/json"
            }   
        })

        return response.data;

    }catch(error){
        console.log("error in leaving group ", error);
        throw error;
    }
}

export const rejoinGroup = async (groupId, userId) =>{
    try{

        const response = await privateApi.put(`/group/api/rejoin-group/${groupId}`, {
            userId
        }, {
            headers: {
                "Content-Type": "application/json"
            }   
        })

        return response.data;

    }catch(error){
        console.log("error in rejoining group ", error);
        throw error;
    }
}

export const assignAdminRole = async (groupId, userId) => {
    try{
        const response = await privateApi.put(`/group/api/assign-admin/${groupId}`, {
            userId
        }, {
            headers: {
                "Content-Type": "application/json"
            }   
        })
        return response.data;

    }catch(error){
        console.log("error in assigning admin role ", error);   
        throw error;
    }
}