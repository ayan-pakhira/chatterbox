import React, { useEffect, useState } from "react";
import { UserPlus, UserMinus, MessageCircle, Check, X, Search } from "lucide-react";
import toast from "react-hot-toast";
import { useNavigate } from "react-router-dom";
import ChatPage from "./ChatPage.jsx";
import { useAuth } from '../context/AuthContext.jsx';
import { useChat } from '../context/ChatContext.jsx';

import {getAllUsers, 
        sendFriendRequest, 
        acceptFriendRequest, 
        rejectFriendRequest, 
        getFriendRequests, 
        getFriendList
      } from '../services/UserServices.js'

function FriendsPage() {

  const [localActiveTab, setLocalActiveTab] = useState("Friends");
  const [query, setQuery] = useState("");
  const[allUsers, setAllUsers] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);
  const [friendRequests, setFriendRequests] = useState([]);
  const [friends, setFriends] = useState([]);


  const {user, isAuthenticated, logout} = useAuth();

  const { handleOpenOrCreateChat, setSelectedChat } = useChat();

  const navigate = useNavigate();

  const fetchUsers = async (searchQuery = "") => {
    setLoading(true);

    try{

      const users = await getAllUsers(searchQuery, page);
      setAllUsers(users.content || []);
      

    }catch(error){
        console.log("error in fetching users ", error)
        setAllUsers([]);
    }finally{
      setLoading(false);
    }
  }

  useEffect(() => {
    if(query != ""){
      fetchUsers(query);

    }
  }, [query])

   

   const handleSearch = (e) => {
    e.preventDefault();
    setPage(0); // Reset to first page on new search
    getAllUsers(query, 0).then(data => setAllUsers(data.content));
  };

  const handleAddFriend = async (receiverId) => {
    try{

      const response = await sendFriendRequest(receiverId);
      console.log("friend request sent ", response);
      toast.success("Friend request sent successfully");

    }catch(error){
      console.log("error in adding friend ", error)
      toast.error("Failed to send friend request. Please try again.");
    }
  }

  const handleAcceptRequest = async (senderId) => {

    try{

      const response = await acceptFriendRequest(senderId);
      console.log("friend request accepted ", response);
      toast.success("Friend request accepted successfully");

    }catch(error){
      console.log("error in accepting friend request ", error)
      toast.error("Failed to accept friend request. Please try again.");
      throw error;
    }
  }

  const handleRejectRequest = async (senderId) => {
    try{

      const response = await rejectFriendRequest(senderId);
      console.log("friend request rejected ", response);
      toast.success("Friend request rejected successfully");

    }catch(error){
      console.log("error in rejecting friend request ", error)
      toast.error("Failed to reject friend request. Please try again.");
      throw error;
    }
  }

  //fetch friend requests
  const friendRequestsList = async () => {
    try{

      const response = await getFriendRequests();
      setFriendRequests(response || []);
      console.log("friend requests ", response);

    }catch(error){
      console.log("error in fetching friend requests ", error)
      toast.error("Failed to fetch friend requests. Please try again.");
    }
  }

  useEffect(() => {
    if(localActiveTab === "Requests"){
      friendRequestsList();
    }
  }, [localActiveTab])

 

  //fetching friends list
  const fetchFriendList = async () => {
    try{
      const response = await getFriendList();
      setFriends(response || []);
      console.log("friend list ", response);


    }catch(error){
      console.log("error in fetching friend list ", error)
      toast.error("Failed to fetch friend list. Please try again.");
      throw error;
    }
  }

  useEffect(() => {
    if(localActiveTab === "Friends"){
      fetchFriendList();
    }
  }, [localActiveTab])


  //Handle message button
  const handleMessageClick = async (friend) => {
    const chat = await handleOpenOrCreateChat(friend.id); 
    if (chat) setSelectedChat(chat);
  };

  

  

  const renderUsers = () => {
  switch (localActiveTab) {
    case "All":
      return allUsers.map((user) => (
        
        <div
          key={user.id}
          className="bg-gray-800/50 rounded-xl p-4 flex flex-col items-center text-center shadow hover:bg-gray-800/80 transition"
        >
          {/* Default avatar */}
          <img
            className="w-16 h-16 rounded-full mb-3"
          />
          <h3 className="text-lg font-medium">{user.userName}</h3>
          <p className="text-sm text-gray-300">{user.email}</p>
          <button
          onClick={() => handleAddFriend(user.id)}
          className="mt-3 flex items-center space-x-1 px-3 py-1 bg-blue-500 text-sm rounded-lg hover:bg-blue-600 transition">
            <UserPlus size={16} />
            <span>Add</span>
          </button>
        </div>
      ));

    case "Friends":
      return friends.map((friend) => (
        <div
          key={friend.id}
          className="bg-gray-800/50 rounded-xl p-4 flex flex-col items-center text-center shadow hover:bg-gray-800/80 transition"
        >
          <img
            // src={friend.avatar}
            // alt={friend.name}
            className="w-16 h-16 rounded-full mb-3"
          />
          <h3 className="text-lg font-medium">{friend.userName}</h3>
          <p className="text-sm text-gray-300">{friend.email}</p>
          <div className="flex mt-3 space-x-2">
            <button
            onClick={()=> handleMessageClick(friend)}
             className="flex items-center space-x-1 px-3 py-1 bg-green-500 text-sm rounded-lg hover:bg-green-600 transition">
              <MessageCircle size={16} />
              <span>Message</span>
            </button>
            <button className="flex items-center space-x-1 px-3 py-1 bg-red-500 text-sm rounded-lg hover:bg-red-600 transition">
              <UserMinus size={16} />
              <span>Remove</span>
            </button>
          </div>
        </div>
      ));

    case "Requests":
      return friendRequests.map((req) => (
        
        <div
          key={req.id}
          className="bg-gray-800/50 rounded-xl p-4 flex flex-col items-center text-center shadow hover:bg-gray-800/80 transition"
        >
          <img
            // src={req.avatar}
            // alt={req.name}
            className="w-16 h-16 rounded-full mb-3"
          />
          <h3 className="text-lg font-medium">{req.userName}</h3>
          <p className="text-sm text-gray-300">{req.email}</p>
          <div className="flex mt-3 space-x-2">
            <button
            onClick={() => handleAcceptRequest(req.id)}
             className="flex items-center space-x-1 px-3 py-1 bg-green-500 text-sm rounded-lg hover:bg-green-600 transition">
              <Check size={16} />
              <span>Accept</span>
            </button>
            <button 
            onClick={() => handleRejectRequest(req.id)}
            className="flex items-center space-x-1 px-3 py-1 bg-red-500 text-sm rounded-lg hover:bg-red-600 transition">
              <X size={16} />
              <span>Reject</span>
            </button>
          </div>
        </div>
      ));

    default:
      return null;
  }
};



  return (
    <div className="p-6 text-white min-h-screen bg-gradient-to-br from-gray-950 via-gray-900 to-black">
      {/* Search + Tabs */}
      <div className="flex flex-col md:flex-row md:items-center md:justify-between mb-6 space-y-4 md:space-y-0">
        <div className="flex items-center bg-gray-800 rounded-lg px-3 py-2 w-full md:w-1/3">
          <Search size={18} className="text-gray-400" />
          <input
            type="text"
            placeholder="Search users..."
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            className="bg-transparent outline-none ml-2 text-sm w-full"
          />
          <button className="ml-3 px-3 py-1 bg-blue-500 text-sm rounded-lg hover:bg-blue-600 transition">
            Search
          </button>
        </div>

        <div className="flex space-x-6 text-lg font-medium">
          {["All", "Friends", "Requests"].map((tab) => (
            <button
              key={tab}
              onClick={() => setLocalActiveTab(tab)}
              className={`pb-1 transition ${
                localActiveTab === tab
                  ? "text-blue-400 border-b-2 border-blue-500"
                  : "text-gray-400 hover:text-white"
              }`}
            >
              {tab}
            </button>
          ))}
        </div>
      </div>

      {/* Users Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
        {renderUsers()}
      </div>
    </div>
  );
}

export default FriendsPage;
