import React, { useState, useEffect, useRef } from "react";
import { Search, Send } from "lucide-react";
import { useAuth } from "../context/AuthContext.jsx";
import { useChat } from "../context/ChatContext.jsx";
import { useMessage } from "../context/MessageContext.jsx";

function ChatPage() {
  const { user } = useAuth();
  const {
    chatList,
    selectedChat,
    setSelectedChat,
    fetchChats,
    addOrUpdateChat,
  } = useChat();

  const { messagesByChat, sendMessage, getMessages, isLoadingMessages } =
    useMessage();

  const [input, setInput] = useState("");
  const messagesEndRef = useRef(null);

  //fetch chat list on load
  useEffect(() => {
    if (user) {
      fetchChats();
    }
  }, [user]);

  //fetch messages when selected chat changes
  useEffect(() => {
    if (selectedChat) {
      getMessages(selectedChat.id);
    }
  }, [selectedChat]);

  //Scroll to bottom when messages update
  useEffect(() => {
    if (messagesEndRef.current) {
      messagesEndRef.current.scrollIntoView({ behavior: "smooth" });
    }
  }, [messagesByChat, selectedChat]);

  //Clear Chat (optional local clear)
  const clearMessages = () => {
    // Now handled in MessageContext if needed
    clearMessages(selectedChat.id);
  };

  //Send Message
  const handleSend = () => {
    if (!input.trim() || !selectedChat) return;
    sendMessage(input);
    setInput("");
  };

  const handleKeyDown = (e) => {
    if (e.key === "Enter") handleSend();
  };

  //Get messages for selected chat
  const currentMessages = selectedChat
    ? messagesByChat[selectedChat.id] || []
    : [];

  //UI
  return (
    <div className="h-screen w-screen bg-gradient-to-br from-gray-950 via-black to-gray-900 p-4 flex gap-4 overflow-hidden">
      {/* ---------- LEFT: Conversations ---------- */}
      <aside className="w-1/3 flex flex-col bg-gray-900/80 rounded-2xl shadow-lg p-4 overflow-hidden">
        {/* Search bar */}
        <div className="flex items-center bg-gray-800 rounded-xl px-3 py-2 mb-4">
          <Search className="w-5 h-5 text-gray-400" />
          <input
            type="text"
            placeholder="Search chats"
            className="ml-2 bg-transparent outline-none w-full text-gray-200 placeholder-gray-400"
          />
        </div>

        {/* Scrollable conversation list */}
        <div className="space-y-3 overflow-y-auto pr-1 flex-1">
          {Array.isArray(chatList) && chatList.length > 0 ? (
            chatList.map((chat) => (
              <div
                key={chat.id}
                onClick={() => setSelectedChat(chat)}
                className={`flex flex-col px-4 py-3 rounded-xl cursor-pointer transition
            ${
              selectedChat?.id === chat.id
                ? "bg-gradient-to-r from-blue-700 to-purple-700"
                : "bg-gray-800 hover:bg-gray-700"
            }`}
              >
                <span className="font-semibold text-white">
                  {chat.chatName || "unnamed chat"}
                </span>
                <span className="text-sm text-gray-400 truncate">
                  {chat.lastMessage}
                </span>
              </div>
            ))
          ) : (
            <p className="text-gray-500 text-center">No chats yet</p>
          )}
        </div>
      </aside>

      {/* ---------- RIGHT: Chat Window ---------- */}
      <main className="w-[68%] flex flex-col bg-gray-900/80 rounded-2xl shadow-lg overflow-hidden">
        {/* Chat Header */}

        {!selectedChat && (
          <div className="flex-1 p-6 flex items-center justify-center text-gray-400">
            Select a chat to start messaging
          </div>
        )}

        {/* Scrollable Messages Area */}
        {selectedChat && (
          <div
            id="chat-messages"
            className="w-[100%] p-6 overflow-y-auto space-y-4 flex flex-col h-[100%] max-h-37rem"
          >
            {isLoadingMessages ? (
              <div className="text-center text-gray-500">
                Loading messages...
              </div>
            ) : currentMessages.length === 0 ? (
              <div className="text-center text-gray-400">No messages yet</div>
            ) : (
              currentMessages.map((msg) => (
                <div
                  key={msg.id}
                  className={`max-w-xs px-4 py-2 rounded-lg ${
                    msg.fromMe
                      ? "bg-blue-700 text-white self-end"
                      : "bg-gray-700 text-white self-start"
                  }`}
                >
                  <p>{msg.content}</p>
                  {/* <span className="text-xs opacity-70 block mt-1">
                    {new Date(msg.timestamp).toLocaleTimeString()}
                  </span> */}
                </div>
              ))
            )}
            <div ref={messagesEndRef} />
          </div>
        )}

        {/* Input Box */}
        {selectedChat && (
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
              onClick={handleSend}
              className="ml-3 bg-gradient-to-r from-blue-700 to-purple-700 p-3 rounded-full hover:scale-105 transition-transform"
            >
              <Send className="w-5 h-5 text-white" />
            </button>
          </div>
        )}
      </main>
    </div>
  );
}

export default ChatPage;
