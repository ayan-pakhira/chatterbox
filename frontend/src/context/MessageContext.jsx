import React, {useState, useEffect, useContext} from 'react'
import { createContext } from 'react'
import { useAuth } from '../context/AuthContext.jsx';
import { useChat } from '../context/ChatContext.jsx';
import {fetchMessages} from '../services/UserServices.js'
import toast from "react-hot-toast";
import { connect, sendWsMessage, disconnect } from "../services/WebSocketServices.js";
import axios from 'axios';

const MessageContext = createContext();

export const MessageProvider = ({ children }) => {

    const { user } = useAuth();
    const { selectedChat, addOrUpdateChat, chatList, setChatList } = useChat();

    const [messagesByChat, setMessagesByChat] = useState({});
    const [isLoadingMessages, setIsLoadingMessages] = useState(false);
    const [messageError, setMessageError] = useState(null);

    // Function to fetch messages for a specific chat
    const getMessages = async (chatId) => {
      if (!user || !chatId) return;

      try {
        setIsLoadingMessages(true);
        setMessageError(null);

        const response = await fetchMessages(chatId);

        const messagesArray = Array.isArray(response) ? response : [];

        //Normalize timestamp once & sort safely
        const normalized = messagesArray
          .map((msg) => {
            const parsedTime = msg.time
              ? new Date(msg.time).getTime()
              : Date.now();

            return {
              ...msg,
              timestamp: isNaN(parsedTime) ? Date.now() : parsedTime,
              fromMe: msg.senderId === user.id,
            };
          })
          .sort((a, b) => a.timestamp - b.timestamp);

        setMessagesByChat((prev) => ({
          ...prev,
          [chatId]: normalized,
        }));
      } catch (error) {
        console.error("Error fetching messages: ", error);
        setMessageError(error || "Failed to fetch messages");
        throw error;
      } finally {
        setIsLoadingMessages(false);
      }
    };



    // Function to send messages
    const sendMessage = async (content) => {
        if(!user || !selectedChat || !content.trim()) return;

        const chatId = selectedChat.id;


        const newMsg = {
            content,
            fromMe: true,
            time: Date.now(),
            senderId: user.id,
            chatId,
        };

        setMessagesByChat((prev) => ({
            ...prev,
            [chatId]: [...prev[chatId] || [], newMsg]
        }));

        addOrUpdateChat({...selectedChat, lastMessage: content});

        try{

            await fetch("http://localhost:8080/chat/message", {
            method: "POST",
            headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${user.token}`
            },
            body: JSON.stringify({
            chatId,
            content,
            }),
         });

        }catch(error){
            console.error( error);
            toast.error("Failed to save messages to db");
            throw error;    
        }

        // Send via WebSocket
        try {
            sendWsMessage(user.id, chatId, content);
        } catch (err) {
            console.error("Failed to send WebSocket message:", err);
        }
    }

     
    //Clear messages for selected chat
    const clearMessages = () => {
        if (!selectedChat) return;
        setMessagesByChat((prev) => ({
        ...prev,
        [selectedChat.id]: [],
        }));

        // Optionally, also clear lastMessage in chatList
        setChatList(chatList.map(chat =>
        chat.id === selectedChat.id ? { ...chat, lastMessage: "" } : chat
        ));
    };


    //WebSocket connection
    useEffect(() => {
        if (!user) return;

        connect(user.id, user.token, (message) => {
        const chatId = message.senderId === user.id ? message.chatId : message.senderId;

        // Update messagesByChat
        setMessagesByChat((prev) => ({
            ...prev,
            [chatId]: [...(prev[chatId] || []), { ...message, fromMe: message.senderId === user.id }],
        }));

        // Update lastMessage in chatList
        setChatList((prev) => prev.map(chat =>
            chat.id === chatId ? { ...chat, lastMessage: message.content } : chat
        ));
        }).catch(err => console.error("WebSocket connection failed:", err));

        return () => disconnect();
    }, [user]);


  
    

    return (
        <MessageContext.Provider value={{
            messagesByChat,
            getMessages,
            sendMessage,
            isLoadingMessages,
            clearMessages,
            messageError

        }}>
           { children}
        </MessageContext.Provider>
    );

};

export const useMessage = () => useContext(MessageContext);