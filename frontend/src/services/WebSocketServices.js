// WebSocketServices.js
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

let stompClient = null;
let subscription = null;
let groupSubscriptions = {};
let isConnected = null;
let pendingMessage = [];

let groupStompClient = null;
let isGroupConnected = false;

//Connect to WebSocket server for group chats
export const connectGroup = (token, groupId, onGroupMessageReceived) => {

  return new Promise((resolve, reject) => {
    if(!token){
      console.error("Cannot connect WebSocket: token is undefined");
      reject("No token");
      return;
    }

    if(!groupId){
      console.error("Cannot connect WebSocket: groupId is undefined");
      reject("No groupId");
      return;
    }

    const socketUrl = `${import.meta.env.VITE_API_URL || "http://localhost:8080"}/group-message`;
    const socket = new SockJS(socketUrl);

    groupStompClient = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      
      connectHeaders: {
        Authorization: `Bearer ${token}`
      },

      onConnect: () => {
        console.log("Connected to Group WebSocket (STOMP CONNECTED).");
        isGroupConnected = true;

        // subscribe to group topic
        try{

          groupStompClient.subscribe(`/topic/group/${groupId}`, (msg) => {
            if(!msg.body) return;

            const message = JSON.parse(msg.body);
            console.log("Group message received via WebSocket: ", message);
            onGroupMessageReceived(message);
          })

          console.log(`Subscribed to group topic /topic/group/${groupId}`);

        }catch(error){
          console.error("error in subscribing to group topic ", error);
        }
        resolve();
      },

      onStompError: (frame) => {
        console.error("STOMP error: ", frame.headers?.message, frame.body);
        reject(frame.headers?.message);
      },

      onWebSocketError: (error) => {
          console.error("WebSocket error: ", error);
          reject(error);
      },

      onDisconnect: () => {
        console.log("Disconnected from Group WebSocket");
        isGroupConnected = false;
      }

    });

    groupStompClient.activate();
  });
};

export const getGroupStompClient = () => {
  return groupStompClient;
}

//connect to WebSocket server for private chats
export const connect = (userId, token, onMessageReceived) => {
    return new Promise((resolve, reject) => {

    if (!userId) {
        console.error("Cannot connect WebSocket: userId is undefined");
        reject("No User id");
        return;
    }
  if (!token) {
    console.error("Cannot connect WebSocket: token is undefined");
    reject("No token");
    return;
  }

  const socketUrl = `${import.meta.env.VITE_API_URL || "http://localhost:8080"}/chat`;
  const socket = new SockJS(socketUrl);

  stompClient = new Client({
    webSocketFactory: () => socket,
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
    debug: (str) => console.log(str),

    // Send Authorization header in the STOMP CONNECT frame
    connectHeaders: {
      Authorization: `Bearer ${token}`
    },

    onConnect: (frame) => {
      console.log("Connected to WebSocket (STOMP CONNECTED).");
      //console.log("STOMP frame:", frame);

      isConnected = true;

      // unsubscribe any previous subscription
      if (subscription) {
        try { subscription.unsubscribe(); } catch(e) {}
        subscription = null;
      }

      // subscribe to private topic for this user
      subscription = stompClient.subscribe(`/topic/private.${userId}`, (msg) => {
        if (!msg.body) return;
        const message = JSON.parse(msg.body);
        onMessageReceived(message);
      });

      resolve();
    },

    onStompError: (frame) => {
      console.error("STOMP error: ", frame.headers?.message, frame.body);
       reject(frame.headers?.message);
    },

    onWebSocketError: (error) => {
        console.error("WebSocket error: ", error);
        reject(error);
    },


    onDisconnect: () => {
      console.log("Disconnected from WebSocket");
    },

    
  });

  stompClient.activate();
 });
 
};



//Unsubscribe from a group (when leaving or switching group)
export const unsubscribeFromGroup = (groupId) => {
  const subscription = groupSubscriptions[groupId];
  if (subscription) {
    subscription.unsubscribe();
    delete groupSubscriptions[groupId];
    console.log(`Unsubscribed from group ${groupId}`);
  }
};


//Send a private message
export const sendWsMessage = (senderId, chatId, content) => {
  if (!stompClient || !isConnected) {
    console.error("WebSocket is not connected yet....");
    pendingMessage.push({senderId, chatId, content})
    return;
  }
  if (!senderId || !chatId || !content) {
    console.error("Invalid message parameters");
    return;
  }
  stompClient.publish({
    destination: '/app/private.sendMessage',
    body: JSON.stringify({ 
      senderId, 
      chatId, 
      content, 
      timestamp: new Date().toISOString() }),
  });
};

//Send a group message
export const sendGroupMessage = (groupId, senderId, content) => {
  if (!stompClient || !isConnected) {
    console.error("WebSocket not connected — cannot send group message.");
    pendingMessage.push({ groupId, senderId, content });
    return;
  }
  stompClient.publish({
    destination: "/app/group.sendMessage",
    body: JSON.stringify({
      groupId,
      senderId,
      content,
      timestamp: new Date().toISOString(),
    }),
  });
};

export const disconnect = () => {
  if (subscription) {
    try { subscription.unsubscribe(); } catch(e) {}
    subscription = null;
  }

  Object.values(groupSubscriptions).forEach(sub => {
    try { sub.unsubscribe(); } catch(e) {}
  });
  groupSubscriptions = {};

  if (stompClient) {
    stompClient.deactivate();
    stompClient = null;
  }

  console.log("disconnected all subscriptions and stomp client");
};
