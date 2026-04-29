import React, {useState, useEffect, useContext} from 'react'
import { createContext } from 'react'
import { useAuth } from '../context/AuthContext.jsx';
import { fetchConversationList, openOrCreateChat } from '../services/UserServices.js';



const ChatContext = createContext();

export const ChatProvider = ({ children }) => {

    const {user, isAuthenticated} = useAuth();

    const [chatList, setChatList] = useState([]);
    const [selectedChat, setSelectedChat] = useState(null);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);


    //function to open or create chat
    const handleOpenOrCreateChat = async (receiverId) => {

        if(!user || !receiverId) return;
        try{
        setIsLoading(true);
        const response = await openOrCreateChat(receiverId);
        console.log("chat opened or created ", response);


        const newChat = response;

        // Check if the chat already exists in chatList
        setChatList((prevChats) => {
            const exists = prevChats.find((chat) => chat.id === newChat.id);
            if(exists){
                return prevChats; 
            }
            return [newChat, ...prevChats]; 
        })

        setSelectedChat(newChat);
        return newChat;

        }catch (err) {
            console.error("Error opening/creating chat:", err);
            toast.error("Failed to open chat");
        } finally {
            setIsLoading(false);
        }
    }


    //function to fetch chat list
    const fetchChats = async () => {
        if(!user) return;

        try{

            setIsLoading(true);
            setError(null);

            const response = await fetchConversationList();
            console.log("chat list fetched ", response);

            if(response){
                setChatList(response);
            }else{
                setChatList([]);
            }


        }catch(error){
            console.log("error in fetching chat list ", error);
            setError(error);
        }finally{
            setIsLoading(false);
        }
    }



    
  // Add or update chat manually
  const addOrUpdateChat = (newChat) => {
    setChatList((prevChats) => {

      const exists = prevChats.find((c) => c.id === newChat.id);
      if (exists) {
        return prevChats.map((c) => (c.id === newChat.id ? newChat : c));
      }
      return [...prevChats, newChat]; // prepend to top
    });
  };


  useEffect(() => {
    if(isAuthenticated){
        fetchChats();
    }else{
        setChatList([]);
        setSelectedChat(null);
    }
  }, [isAuthenticated, user]);

  
  return (
    <ChatContext.Provider value={{
        chatList,
        setChatList, 
        selectedChat, 
        setSelectedChat, 
        fetchChats, 
        handleOpenOrCreateChat, 
        addOrUpdateChat, 
        isLoading, 
        error}}>
            {children}
        </ChatContext.Provider>
  );

};

export const useChat = () =>  useContext(ChatContext) 