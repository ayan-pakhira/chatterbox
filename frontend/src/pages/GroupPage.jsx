import React, { useState, useEffect } from "react";
import { useRef } from "react";
import { Search, Send, Plus, X, MoreVertical, Edit } from "lucide-react";
import { motion } from "framer-motion";
import toast from "react-hot-toast";
import { useAuth } from "../context/AuthContext.jsx";
import {
  fetchGroupChatLists,
  getFriendList,
  createGroupChat,
  fetchGroupMessages,
  fetchGroupMembers,
  fetchGroupAdmins,
  editGroupName,
  deleteGroupByAdmin,
  deleteGroupByUser,
  addMembersToGroup,
  removeMemberFromGroup,
  assignAdminRole,
  leaveGroupByUser
} from "../services/UserServices.js";

import {
  connectGroup,
  disconnect,
  sendGroupMessage,
} from "../services/WebSocketServices.js";

function GroupPage() {
  const [activeGroup, setActiveGroup] = useState(null);
  const [groups, setGroups] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const { user, isAuthenticated } = useAuth();
  const [friends, setFriends] = useState([]);

  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState("");

  const [showModal, setShowModal] = useState(false);
  const [selectedFriends, setSelectedFriends] = useState([]);
  const [selectAll, setSelectAll] = useState(false);

  const [viewMode, setViewMode] = useState("CHAT"); //CHAT or GROUP

  const [groupMembers, setGroupMembers] = useState([]);
  const [groupAdmins, setGroupAdmins] = useState([]);

  const [isEditingGroupName, setIsEditingGroupName] = useState(false);
  const [newGroupName, setNewGroupName] = useState(activeGroup?.groupName);
  const [newGroup, setNewGroup] = useState(null);

  //add member to group state
  const [showAddMemberModal, setShowAddMemberModal] = useState(false);
  const [selectedNewMembers, setSelectedNewMembers] = useState([]);
  const [selectAllNew, setSelectAllNew] = useState(false);
  const [addingMembers, setAddingMembers] = useState(false);

  //New: Group name field
  const [groupName, setGroupName] = useState("");

  const [confirmAction, setConfirmAction] = useState(null);

  const [openMenuUserId, setOpenMenuUserId] = useState(null);
  const menuRef = useRef(null);

  const isAdmin = groupAdmins.some((admin) => admin.userId === user?.id);

  const userToken = JSON.parse(localStorage.getItem("user"));
  const token = userToken?.token;

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (menuRef.current && !menuRef.current.contains(e.target)) {
        setOpenMenuUserId(null);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  //function to fetch the friends list
  const fetchFriends = async () => {
    if (!user) return;

    try {
      setLoading(true);
      setError(null);

      const response = await getFriendList();
      console.log("fetched friends ", response);

      if (response) {
        setFriends(response);
      } else {
        setFriends([]);
      }
    } catch (error) {
      console.log("error in fetching friends ", error);
      throw error;
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (isAuthenticated) {
      fetchFriends();
    }
  }, [user, isAuthenticated]);

  //function to fetch group chats
  const fetchGroupChats = async () => {
    if (!user) return;

    try {
      setLoading(true);
      setError(null);

      const response = await fetchGroupChatLists();
      console.log("fetched group chats ", response);

      if (response) {
        setGroups(response);
      } else {
        setGroups([]);
      }
    } catch (error) {
      console.log("error in fetching group chats ", error);
      setError(error);
    } finally {
      setLoading(false);
    }
  };

  //fetch group chats on load
  useEffect(() => {
    if (isAuthenticated) {
      fetchGroupChats();
    }
  }, [user, isAuthenticated]);

  //fetch group members
  const fetchMembers = async (groupId) => {
    if (!user || !groupId) return;

    try {
      setLoading(true);
      setError(null);

      const response = await fetchGroupMembers(groupId);
      console.log("fetched group members ", response);

      if (response) {
        setGroupMembers(response.members);
      } else {
        setGroupMembers([]);
      }
    } catch (error) {
      console.log("error in fetching group members ", error);
      setError(error);
    } finally {
      setLoading(false);
    }
  };

  //fetch group members on load
  useEffect(() => {
    if (isAuthenticated && activeGroup) {
      fetchMembers(activeGroup.id);
    }
  }, [activeGroup, isAuthenticated]);

  //fetch group admins
  const fetchAdmins = async (groupId) => {
    if (!user || !groupId) return;

    try {
      setLoading(true);
      setError(null);

      const response = await fetchGroupAdmins(groupId);
      console.log("fetched group admins ", response);

      if (response) {
        setGroupAdmins(response.groupAdmins);
      } else {
        setGroupAdmins([]);
      }
    } catch (error) {
      console.log("error in fetching group admins ", error);
      setError(error);
    } finally {
      setLoading(false);
    }
  };

  //fetch group admins on load
  useEffect(() => {
    if (isAuthenticated && activeGroup) {
      fetchAdmins(activeGroup.id);
    }
  }, [activeGroup, isAuthenticated]);

  //create a new group
  const handleConfirmButton = async () => {
    try {
      if (!user) return;

      if (!groupName.trim()) {
        toast.error("Please enter a group name.");
        return;
      }

      //extracting the ids of selected friends and storing them in array.
      const userIds = selectedFriends;
      if (userIds.length === 0) {
        toast.error("Please select at least one friend to create a group.");
        return;
      }
      console.log("Sending group creation request with IDs:", userIds);

      const newGroup = await createGroupChat(user.id, groupName, userIds);
      console.log("created new group ", newGroup);
      toast.success("Group created successfully!");

      //refresh group list
      fetchGroupChats();
      setShowModal(false);
      setGroupName("");
      setSelectedFriends([]);
      setSelectAll(false);
    } catch (error) {
      console.log("error in creating group ", error);
      toast.error("Failed to create group. Please try again.");
      throw error;
    }
  };

  //   useEffect(() => {
  //     if (groups.length > 0 && !activeGroup) {
  //       setActiveGroup(groups);   // DEFAULT SELECTION
  //     }
  //  }, [groups]);

  //group setting related featutres
  const handleUpdateGroupName = async () => {
    try {
      if (!newGroupName.trim()) return;

      const response = await editGroupName(activeGroup.id, newGroupName);
      console.log("updated group name response ", response);
      toast.success("Group name updated successfully!");
      setActiveGroup(response);
      setIsEditingGroupName(false);

      //refresh group list to reflect the new name
      fetchGroupChats();
      fetchGroupMessages(activeGroup.id);
    } catch (error) {
      console.log("error in updating group name ", error);
      toast.error("Failed to update group name. Please try again.");
    }
  };

  //keep the group name on load after changing the group name.
  useEffect(() => {
    if (isAuthenticated && activeGroup) {
      setNewGroupName(activeGroup.groupName);
    }
  }, [activeGroup, isAuthenticated]);

  //remove member from group
  const handleRemoveGroupMember = async () => {
    try {

      const memberId = groupMembers.find((m) => m.userId === confirmAction.userId)?.userId;
      const response = await removeMemberFromGroup(activeGroup.id, memberId);
      console.log("removed member response ", response);
      toast.success("Member removed successfully!");

      setActiveGroup(response);

      //refresh group members list
      fetchMembers(activeGroup.id);
      fetchGroupChats();
    } catch (error) {
      console.log("error in removing group member ", error);
    }
  };

  //refresh after group member updates.
  useEffect(() => {
    if (activeGroup?.id) {
      fetchMembers(activeGroup.id);
    }
  }, [activeGroup?.id]);

  //assign admin role to member
  const handleAssignAdminRole = async (memberId) => {
    try {
      const response = await assignAdminRole(activeGroup.id, memberId);

      console.log("assign admin role response ", response);
      toast.success("Admin role assigned successfully!");

      setActiveGroup(response);
      //refresh group members and admins list
      fetchMembers(activeGroup.id);
      fetchAdmins(activeGroup.id);
    } catch (error) {
      console.log("error in assigning admin role ", error);
    }
  };

  // // load to fetch group members and admins after assigning admin role
  // useEffect(() => {
  //   if(activeGroup?.id){
  //     fetchMembers(activeGroup.id);
  //     fetchAdmins(activeGroup.id);
  //   }
  // }, [activeGroup?.id]);

  //add member to group

  const toggleNewMember = (id) => {
    setSelectedNewMembers((prev) =>
      prev.includes(id) ? prev.filter((m) => m !== id) : [...prev, id],
    );
  };

  const handleSelectAllNew = () => {
    if (selectAllNew) {
      setSelectedNewMembers([]);
    } else {
      setSelectedNewMembers(friends.map((f) => f.id));
    }
    setSelectAllNew(!selectAllNew);
  };

  //function to handle adding members to group
  const handleAddMembersConfirm = async () => {
    if (!selectedNewMembers.length) return;

    try {

      const newMembers = selectedNewMembers.filter(id => typeof id === "string");
      if(newMembers.length === 0){
        toast.error("Please select at least one member to add.");
        return;
      }
      console.log("Adding members with IDs: ", newMembers);

      const response = await addMembersToGroup(activeGroup.id, newMembers);
      console.log("add members to group response ", response);

      toast.success("Member added successfully!");

      setShowAddMemberModal(false);
      setSelectedNewMembers([]);
      setSelectAllNew(false);
      //refresh group members list
      fetchMembers(activeGroup.id);
    } catch (error) {
      console.log("error in adding member to group ", error);
      toast.error("Failed to add member. Please try again.");
    } finally {
      setAddingMembers(false);
    }
  };

  useEffect(() => {
    if (activeGroup?.id) {
      fetchMembers(activeGroup.id);
    }
  }, [activeGroup?.id]);

  const handleLeaveGroup = async () => {
    try{
      const gId = activeGroup.id;
      console.log("leaving group with id ", gId);
      const response = await leaveGroupByUser(activeGroup.id);
      console.log("leave group response ", response);
      toast.success("You have left the group.");

      setActiveGroup(null);
      //refresh group list
      fetchGroupChats();


    }catch(error){
      console.log("error in leaving group ", error);
      toast.error("Failed to leave group. Please try again.");
    }
  }

  useEffect(() => {
    if(activeGroup?.id){
      fetchGroupChats();
    }
  }, [activeGroup?.id]);

  const handleDeleteGroupByAdmin = async () => {

    try{

      const gId = activeGroup.id;
      console.log("deleting group with id ", gId);
      const response = await deleteGroupByAdmin(activeGroup.id);
      console.log("delete group response ", response);
      toast.success("Group deleted successfully.");
      setActiveGroup(null);
      //refresh group list
      fetchGroupChats();

    }catch(error){
      console.log("error in deleting group by admin ", error);
      toast.error("Failed to delete group. Please try again.");
    }
  }

  useEffect(() => {
    if(activeGroup?.id){
      fetchGroupChats();
    }
  }, [activeGroup?.id]);

  //websocket connection for group messages
  useEffect(() => {
    if (!activeGroup || !token) return;

    setMessages([]); //clear previous messages

    //load previous messages for this group
    const loadMessages = async () => {
      try {
        const messages = await fetchGroupMessages(activeGroup.id);
        console.log("fetched group messages ", messages);
        setMessages(messages || []);
      } catch (error) {
        console.log("error in fetching group messages ", error);
        throw error;
      }
    };
    loadMessages();

    //connect to websocket for this group
    connectGroup(token, activeGroup.id, (incomingMsg) => {
      setMessages((prev) => [...prev, incomingMsg]);
    }).catch((error) =>
      console.error("group WebSocket connection error: ", error),
    );
  }, [activeGroup, token]);

  //function to send message
  const sendMessage = () => {
    if (!input.trim()) return;

    sendGroupMessage(activeGroup.id, user.id, input);

    setMessages((prev) => [
      ...prev,
      {
        senderId: user.id,
        content: input,
        timestamp: new Date().toISOString(),
        fromMe: true,
      },
    ]);
    setInput("");
  };

  const handleKeyDown = (e) => {
    if (e.key === "Enter") sendMessage();
  };

  //hanlde confirm action for removing member, leaving group, deleting group
  const hanldeConfirmAction = async () => {
    try {
      if (!confirmAction) return;
      setLoading(true);

      switch (confirmAction.type) {
        case "REMOVE":
          await handleRemoveGroupMember();
          break;

        case "DELETE":
          await handleDeleteGroupByAdmin();
          break;

        case "LEAVE":
          await handleLeaveGroup();
          break;

        default:
          return;
      }

      setConfirmAction(null);
    } catch (error) {
      console.log("error in confirming action ", error);
      toast.error("Failed to perform action. Please try again.");
    } finally {
      setLoading(false);
    }
  };
  // Checkbox handlers
  const toggleFriend = (id) => {
    setSelectedFriends((prev) =>
      prev.includes(id) ? prev.filter((f) => f !== id) : [...prev, id],
    );
  };

  const handleSelectAll = () => {
    if (selectAll) {
      setSelectedFriends([]);
      setSelectAll(false);
    } else {
      setSelectedFriends(friends.map((f) => f.id));
      setSelectAll(true);
    }
  };

  if (loading)
    return <p className="text-center text-gray-500">Loading groups...</p>;
  if (error) return <p className="text-center text-red-500">Error: {error}</p>;

  return (
    <div className="relative h-screen w-screen bg-gradient-to-br from-gray-950 via-gray-900 to-gray-800 p-4 flex gap-4 overflow-hidden">
      {/* ---------- LEFT: Groups List ---------- */}
      <aside className="relative w-1/3 flex flex-col bg-gray-900/85 rounded-2xl shadow-lg p-4 overflow-hidden">
        {/* Search bar */}
        <div className="flex items-center bg-gray-800 rounded-xl px-3 py-2 mb-4">
          <Search className="w-5 h-5 text-gray-400" />
          <input
            type="text"
            placeholder="Search groups"
            className="ml-2 bg-transparent outline-none w-full text-gray-200 placeholder-gray-400"
          />
        </div>

        {/* Scrollable groups list */}
        <div className="space-y-3 overflow-y-auto pr-1 flex-1">
          {groups.map((grp) => (
            <div
              key={grp.id}
              onClick={() => setActiveGroup(grp)} //when clicked, mark active
              className={`flex flex-col px-4 py-3 rounded-xl cursor-pointer transition
                ${
                  activeGroup?.id === grp.id
                    ? "bg-gradient-to-r from-teal-700 to-indigo-700"
                    : "bg-gray-800 hover:bg-gray-700"
                }`}
            >
              <span className="font-semibold text-white">{grp.groupName}</span>
              <span className="text-sm text-gray-400 truncate">
                {grp.lastMessage || "No messages yet"}
              </span>
            </div>
          ))}
        </div>

        {/* Add group button */}
        <motion.button
          whileHover={{ scale: 1.15, boxShadow: "0 10px 20px rgba(0,0,0,0.4)" }}
          whileTap={{ scale: 0.95 }}
          onClick={() => setShowModal(true)}
          className="absolute bottom-6 right-6 p-4 rounded-full bg-gradient-to-r from-teal-700 to-indigo-700 text-white shadow-lg"
        >
          <Plus className="w-7 h-7" />
        </motion.button>
      </aside>

      {/* ---------- RIGHT: Group Chat Window ---------- */}

      <main className="flex-1 flex flex-col bg-gray-900/85 rounded-2xl shadow-lg overflow-hidden">
        {/* ---------- TOP NAV BAR ---------- */}
        <div className="px-6 py-4 flex items-center gap-3 bg-gray-900/60 rounded-t-2xl border-b border-gray-700">
          {viewMode === "GROUP_INFO" && (
            <button
              onClick={() => setViewMode("CHAT")}
              className="text-gray-300 hover:text-white transition"
            >
              ← Back
            </button>
          )}

          {activeGroup && (
            <button
              onClick={() => setViewMode("GROUP_INFO")}
              className="text-lg font-bold text-white hover:text-teal-400 transition"
            >
              {activeGroup.groupName}
            </button>
          )}
        </div>

        {/* ---------- CONTENT AREA ---------- */}
        <div className="flex-1 flex flex-col overflow-hidden">
          {/* ===== CHAT VIEW ===== */}
          {viewMode === "CHAT" && (
            <>
              <div className="flex-1 p-6 overflow-y-auto space-y-4 flex flex-col">
                {messages.map((msg, i) => (
                  <div
                    key={i}
                    className={`max-w-[70%] px-4 py-2 rounded-2xl shadow text-gray-100 
                ${
                  msg.senderUserName === user.userName
                    ? "self-end bg-indigo-700"
                    : "self-start bg-gray-800"
                }`}
                  >
                    {msg.senderUserName !== user.userName && (
                      <div className="text-xs text-teal-300 font-semibold mb-1">
                        {msg.senderUserName}
                      </div>
                    )}

                    {msg.content}
                  </div>
                ))}
              </div>

              {/* INPUT */}
              <div className="flex items-center px-6 py-4 bg-gray-900/60 rounded-b-2xl">
                <input
                  type="text"
                  value={input}
                  onChange={(e) => setInput(e.target.value)}
                  onKeyDown={handleKeyDown}
                  placeholder="Type a message..."
                  className="flex-1 bg-gray-800 text-gray-200 px-4 py-2 rounded-full outline-none placeholder-gray-400"
                />
                <button
                  onClick={sendMessage}
                  className="ml-3 bg-gradient-to-r from-teal-700 to-indigo-700 p-3 rounded-full hover:scale-105 transition-transform"
                >
                  <Send className="w-5 h-5 text-white" />
                </button>
              </div>
            </>
          )}

          {/* ===== GROUP INFO VIEW ===== */}
          {viewMode === "GROUP_INFO" && (
            <div className="flex-1 p-6 text-white space-y-6 overflow-y-auto max-h-full">
              <div className="text-xl font-semibold">Group Settings</div>

              {/* Group Name Section */}
              <div className="bg-gray-800 p-4 rounded-xl">
                <div className="text-sm text-gray-400 mb-2">Group Name</div>

                <div className="flex items-center justify-between">
                  <div className="text-lg font-bold text-teal-400">
                    {activeGroup?.groupName}
                  </div>

                  {isAdmin && (
                    <button
                      onClick={() => setIsEditingGroupName(true)}
                      className="text-gray-400 hover:text-white"
                    >
                      <Edit size={16} />
                    </button>
                  )}
                </div>

                {/* Inline edit */}
                {isEditingGroupName && (
                  <div className="mt-3 flex gap-2">
                    <input
                      type="text"
                      value={newGroupName}
                      onChange={(e) => setNewGroupName(e.target.value)}
                      className="flex-1 bg-gray-700 px-3 py-1 rounded text-sm outline-none"
                    />
                    <button
                      type="button"
                      onClick={handleUpdateGroupName}
                      className="text-sm bg-teal-600 px-3 py-1 rounded"
                    >
                      Save
                    </button>
                    <button
                      onClick={() => setIsEditingGroupName(false)}
                      className="text-sm text-gray-400"
                    >
                      Cancel
                    </button>
                  </div>
                )}
              </div>

              {/* Group Members */}
              <div className="bg-gray-800 p-4 rounded-xl">
                <div className="flex items-center justify-between mb-3">
                  <h3 className="text-sm font-semibold text-gray-300">
                    Group Members
                  </h3>

                  {isAdmin && (
                    <button
                      onClick={() => setShowAddMemberModal(true)}
                      className="flex items-center gap-1 text-sm bg-teal-600 hover:bg-teal-700 px-3 py-1 rounded-lg transition"
                    >
                      + Add Member
                    </button>
                  )}
                  {showAddMemberModal && (
                    <div className="fixed inset-0 flex items-center justify-center bg-black/60 backdrop-blur-sm z-50">
                      <div className="bg-gray-900 text-white p-6 rounded-2xl w-96 relative shadow-xl">
                        {/* Close */}
                        <button
                          onClick={() => setShowAddMemberModal(false)}
                          className="absolute top-4 right-4 text-gray-400 hover:text-white"
                        >
                          <X className="w-5 h-5" />
                        </button>

                        <h3 className="text-lg font-semibold mb-4">
                          Add Members
                        </h3>

                        {/* Select All */}
                        <div className="flex justify-between items-center mb-4">
                          <span className="text-sm text-gray-300">
                            Friend List
                          </span>
                          <label className="flex items-center gap-2 text-sm">
                            <input
                              type="checkbox"
                              checked={selectAllNew}
                              onChange={handleSelectAllNew}
                              className="accent-teal-600 w-4 h-4"
                            />
                            Select All
                          </label>
                        </div>

                        {/* Friends */}
                        <div className="space-y-3 max-h-60 overflow-y-auto pr-2">
                          {friends.map((f) => (
                            <label
                              key={f.id}
                              className="flex items-center gap-2"
                            >
                              <input
                                type="checkbox"
                                checked={selectedNewMembers.includes(f.id)}
                                onChange={() => toggleNewMember(f.id)}
                                className="accent-teal-600 w-4 h-4"
                              />
                              {f.userName}
                            </label>
                          ))}
                        </div>

                        {/* Confirm */}
                        <button
                          onClick={handleAddMembersConfirm}
                          disabled={addingMembers}
                          className="mt-6 w-full bg-gradient-to-r from-teal-700 to-indigo-700 py-2 rounded-xl hover:scale-105 transition-transform disabled:opacity-50"
                        >
                          {addingMembers ? "Adding..." : "Add Members"}
                        </button>
                      </div>
                    </div>
                  )}
                </div>

                {groupMembers?.length > 0 ? (
                  <div className="space-y-2">
                    {groupMembers.map((member) => (
                      <div
                        key={member.userId}
                        className="relative flex items-center justify-between bg-gray-700/60 px-3 py-2 rounded-lg"
                      >
                        {/* Username */}
                        <span className="text-gray-200 text-sm">
                          {member.userName}
                        </span>

                        {/* 3-dot button */}
                        <button
                          onClick={() => setOpenMenuUserId(member.userId)}
                          className="text-gray-400 hover:text-white"
                        >
                          <MoreVertical size={18} />
                        </button>

                        {/* Dropdown menu */}
                        {openMenuUserId === member.userId && (
                          <div
                            ref={menuRef}
                            className="absolute right-2 top-10 bg-gray-900 rounded shadow w-40 z-50"
                          >
                            {/* Visible to ALL */}
                            <button className="w-full px-4 py-2 hover:bg-gray-700 text-left">
                              Message
                            </button>

                            {/* ADMIN ONLY */}
                            {isAdmin && (
                              <>
                                <button
                                  onClick={() =>
                                    handleAssignAdminRole(member.userId)
                                  }
                                  className="w-full px-4 py-2 hover:bg-gray-700 text-left"
                                >
                                  Make Admin
                                </button>

                                <button
                                  onClick={() =>
                                    setConfirmAction({
                                      type: "REMOVE",
                                      userId: member.userId,
                                      userName: member.userName,
                                    })
                                  }
                                  className="w-full px-4 py-2 text-red-400 hover:bg-gray-700 text-left"
                                >
                                  Remove
                                </button>
                              </>
                            )}
                          </div>
                        )}
                      </div>
                    ))}
                  </div>
                ) : (
                  <p className="text-gray-500 text-sm">No members found</p>
                )}
              </div>

              {/* Group Admin */}
              <div className="bg-gray-800 p-4 rounded-xl mt-4">
                <h3 className="text-sm font-semibold text-gray-300 mb-3">
                  Group Admins
                </h3>

                {groupAdmins?.length > 0 ? (
                  <div className="space-y-2">
                    {groupAdmins.map((admin) => (
                      <div
                        key={admin.userId}
                        className="flex items-center bg-gray-700/60 px-3 py-2 rounded-lg"
                      >
                        <span className="text-gray-200 text-sm">
                          {admin.userName}
                        </span>
                      </div>
                    ))}
                  </div>
                ) : (
                  <p className="text-gray-500 text-sm">No admins found</p>
                )}
              </div>
              {/* Group Actions */}
              <div className="bg-gray-800 p-4 rounded-xl space-y-3 border border-gray-700">
                {/* Leave Group (ALL USERS) */}
                <button
                  onClick={() => setConfirmAction({ type: "LEAVE" })}
                  className="w-full text-left text-red-400 hover:bg-gray-700 px-4 py-2 rounded"
                >
                  Leave Group
                </button>

                {/* Delete Group (ADMIN ONLY) */}
                {isAdmin && (
                  <button
                    onClick={() => setConfirmAction({ type: "DELETE" })}
                    className="w-full text-left text-red-500 hover:bg-red-600/20 px-4 py-2 rounded font-semibold"
                  >
                    Delete Group
                  </button>
                )}
              </div>

              {confirmAction && (
                <div className="fixed inset-0 bg-black/60 flex items-center justify-center z-50">
                  <div className="bg-gray-900 p-6 rounded-xl text-white">
                    <p className="mb-4">
                      {confirmAction.type === "REMOVE" &&
                        `Remove ${confirmAction.userName}?`}
                      {confirmAction.type === "DELETE" && "Delete this group?"}
                      {confirmAction.type === "LEAVE" && "Leave this group?"}
                    </p>

                    <div className="flex justify-end gap-3">
                      <button onClick={() => setConfirmAction(null)}>
                        Cancel
                      </button>

                      <button
                        onClick={hanldeConfirmAction}
                        className="bg-red-600 px-4 py-2 rounded"
                      >
                        Confirm
                      </button>
                    </div>
                  </div>
                </div>
              )}
            </div>
          )}
        </div>
      </main>

      {/* <main className="flex-1 flex flex-col bg-gray-900/85 rounded-2xl shadow-lg overflow-hidden">
        <div className="px-6 py-4 flex items-center justify-between bg-gray-900/60 rounded-t-2xl">
          <h2 className="text-xl font-bold text-white">{groups.name}</h2>
        </div>

        <div className="flex-1 p-6 overflow-y-auto space-y-4 flex flex-col">
          {messages.map((msg, i) => (
            <div
              key={i}
              className={`max-w-[70%] px-4 py-2 rounded-2xl shadow text-gray-100 
                ${ msg.senderUserName === user.userName ? "self-end bg-indigo-700" : "self-start bg-gray-800"}`}
            >
              
              {msg.senderUserName !== user.userName && (
                <div className="text-xs text-teal-300 font-semibold mb-1">
                  {msg.senderUserName}
                </div>
              )}

              {msg.content}
            </div>
          ))}
        </div>

        <div className="flex items-center px-6 py-4 bg-gray-900/60 rounded-b-2xl">
          <input
            type="text"
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyDown={handleKeyDown}
            placeholder="Type a message..."
            className="flex-1 bg-gray-800 text-gray-200 px-4 py-2 rounded-full outline-none placeholder-gray-400"
          />
          <button
            onClick={sendMessage}
            className="ml-3 bg-gradient-to-r from-teal-700 to-indigo-700 p-3 rounded-full hover:scale-105 transition-transform"
          >
            <Send className="w-5 h-5 text-white" />
          </button>
        </div>
      </main> */}

      {/* ---------- POPUP MODAL ---------- */}
      {showModal && (
        <div className="absolute inset-0 flex items-center justify-center bg-black/60 backdrop-blur-sm z-50">
          <motion.div
            initial={{ scale: 0.8, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            exit={{ scale: 0.8, opacity: 0 }}
            className="bg-gray-900 text-white p-6 rounded-2xl w-96 relative shadow-xl"
          >
            {/* Close icon */}
            <button
              onClick={() => setShowModal(false)}
              className="absolute top-4 right-4 text-gray-400 hover:text-white"
            >
              <X className="w-5 h-5" />
            </button>

            {/* Group Name Field */}
            <div className="mb-4">
              <label className="block mb-1 text-sm font-medium">
                Group Name
              </label>
              <input
                type="text"
                value={groupName}
                onChange={(e) => setGroupName(e.target.value)}
                placeholder="Enter group name"
                className="w-full bg-gray-800 rounded-xl px-3 py-2 outline-none text-gray-200 placeholder-gray-400"
              />
            </div>

            {/* Header with "Select All" */}
            <div className="flex justify-between items-center mb-4">
              <h3 className="text-lg font-semibold">Friend List</h3>
              <label className="flex items-center gap-2 text-sm">
                <input
                  type="checkbox"
                  checked={selectAll}
                  onChange={handleSelectAll}
                  className="accent-indigo-600 w-4 h-4"
                />
                Select All
              </label>
            </div>

            {/* Friend checkboxes */}
            <div className="space-y-3 max-h-60 overflow-y-auto pr-2">
              {friends.map((f) => (
                <label key={f.id} className="flex items-center gap-2">
                  <input
                    type="checkbox"
                    checked={selectedFriends.includes(f.id)}
                    onChange={() => toggleFriend(f.id)}
                    className="accent-indigo-600 w-4 h-4"
                  />
                  {f.userName}
                </label>
              ))}
            </div>

            {/* Confirm Button */}
            <button
              onClick={handleConfirmButton}
              className="mt-6 w-full bg-gradient-to-r from-teal-700 to-indigo-700 py-2 rounded-xl hover:scale-105 transition-transform"
            >
              Confirm
            </button>
          </motion.div>
        </div>
      )}
    </div>
  );
}

export default GroupPage;
