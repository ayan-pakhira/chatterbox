import React, { useState, useEffect } from "react";
import { Link, NavLink } from "react-router-dom";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext.jsx";
import ChatPage from "../pages/ChatPage.jsx";
import GroupsPage from "../pages/GroupPage.jsx";
import FriendsPage from "../pages/FriendsPage.jsx";
import CallsPage from "../pages/CallPage.jsx";
import { useChat } from "../context/ChatContext.jsx";

function DashboardPage() {
  const [activeTab, setActiveTab] = useState("Chats");
  const { user, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();

  const {fetchChats} = useChat();

  const renderContent = () => {
    switch (activeTab) {
      case "Chats":
        return <ChatPage />;
      case "Groups":
        return <GroupsPage />;
      case "Friends":
        return <FriendsPage setActiveTab={setActiveTab} />;
      case "Calls":
        return <CallsPage />;
      default:
        return null;
    }
  };

  const tabs = [
    { key: "Chats", label: "💬 Chats" },
    { key: "Groups", label: "👥 Groups" },
    { key: "Friends", label: "🤝 Friends" },
    { key: "Calls", label: "📞 Calls" },
  ];

  const userInitial = user?.userName ? user.userName.charAt(0).toUpperCase() : "";

  const handleLogout = () => {
    logout();
    navigate("/");
  };

  

  return (
    <div className="overflow-hidden bg-gradient-to-br from-gray-900 via-black to-gray-800 text-white">
      {/* Navbar */}
      <NavLink className="flex items-center justify-between px-10 py-6 shadow-md">
        <h1 className="text-2xl font-extrabold tracking-wide">ChatterBox</h1>

        {isAuthenticated ? (
          <div className="flex items-center gap-4">
            <div className="w-10 h-10 flex items-center justify-center bg-gradient-to-r from-blue-500 to-purple-600 text-white font-bold rounded-full shadow-md">
              {userInitial}
            </div>

            <button
              onClick={handleLogout}
              className="px-4 py-2 bg-red-500 text-white rounded-lg hover:bg-red-600 transition"
            >
              Logout
            </button>
          </div>
        ) : (
          <Link
            to="/login"
            className="px-5 py-2 bg-blue-600 rounded-xl shadow-lg hover:shadow-blue-500/50 hover:scale-105 transition-transform"
          >
            Login
          </Link>
        )}
      </NavLink>

      {/* Tabs */}
      <div className="flex justify-center space-x-20 border-b border-gray-700">
        {tabs.map((tab) => (
          <button
            key={tab.key}
            onClick={() => setActiveTab(tab.key)}
            className={`relative py-3 text-lg font-medium transition ${
              activeTab === tab.key
                ? "text-blue-400"
                : "text-gray-400 hover:text-white"
            }`}
          >
            {tab.label}
            {activeTab === tab.key && (
              <span className="absolute left-0 right-0 -bottom-1 h-1 bg-blue-500 rounded-full"></span>
            )}
          </button>
        ))}
      </div>

      {/* Content */}
      <div className="p-6">{renderContent()}</div>
    </div>
  );
}

export default DashboardPage;






























// import React, {useState, useEffect} from 'react'
// import { Link, NavLink } from 'react-router-dom'
// import { useNavigate } from 'react-router-dom'
// import { useAuth } from '../context/AuthContext.jsx';
// import toast from 'react-hot-toast';
// import ChatPage from '../pages/ChatPage.jsx';
// import GroupsPage from '../pages/GroupPage.jsx';
// import FriendsPage from '../pages/FriendsPage.jsx';
// import CallsPage from '../pages/CallPage.jsx';



// function DashboardPage() {
//   const [activeTab, setActiveTab] = useState("Chats");
//   const [chatTarget, setChatTarget] = useState(null);
//   const [conversation, setConversation] = useState([]);

//   const {user, isAuthenticated, logout} = useAuth();
//   const navigate = useNavigate();

//   const renderContent = () => {
//     switch (activeTab) {
//       case "Chats":
//         return <ChatPage activeTab={activeTab} chatTarget={chatTarget} conversation={conversation} setConversation={setConversation}/>;
//       case "Groups":
//         return <GroupsPage/>;
//       case "Friends":
//         return <FriendsPage setActiveTab={setActiveTab} setChatTarget={setChatTarget} conversation={conversation} setConversation={setConversation}/>;
//       case "Calls":
//         return  <CallsPage/>;
//       default:
//         return null;
//     }
//   };

//   const tabs = [
//     { key: "Chats", label: "💬 Chats" },
//     { key: "Groups", label: "👥 Groups" },
//     { key: "Friends", label: "🤝 Friends" },
//     { key: "Calls", label: "📞 Calls" },
//   ];

//   const userInitial = user?.userName
//   ? user.userName.charAt(0).toUpperCase()
//   : "";


//   const handleLogout = () => {
//     logout();
//     setConversation([]);
//     navigate('/');
    
//   }

  

// // // On mount, restore conversations
// // useEffect(() => {
// //   const stored = JSON.parse(localStorage.getItem("conversations")) || [];
// //   if (stored.length > 0) {
// //     setConversation(stored);
// //   }
// // }, []);

// // // Whenever conversation changes, save it
// // useEffect(() => {
// //   if (conversation.length > 0) {
// //     localStorage.setItem("conversations", JSON.stringify(conversation));
// //   }
// // }, [conversation]);


//   return (
//     <div className="min-h-screen bg-gradient-to-br from-gray-900 via-black to-gray-800 text-white">
//       {/* Navbar */}
//       <NavLink
//       className="flex items-center justify-between px-10 py-6 shadow-md">
//         <h1 className="text-2xl font-extrabold tracking-wide">ChatterBox</h1>
        
//         {isAuthenticated ? (
//           <div className="flex items-center gap-4">
//             <div className="w-10 h-10 flex items-center justify-center bg-gradient-to-r from-blue-500 to-purple-600 text-white font-bold rounded-full shadow-md">
//               {userInitial}
//             </div>

//             <button
//               onClick={handleLogout}
//               className="px-4 py-2 bg-red-500 text-white rounded-lg hover:bg-red-600 transition"
//             >
//               Logout
//             </button>
//           </div>
//         ):(
//           <Link
//           to="/login"
//           className="px-5 py-2 bg-blue-600 rounded-xl shadow-lg hover:shadow-blue-500/50 hover:scale-105 transition-transform">
//           Login
//         </Link>
//         )}
        
//       </NavLink>

//       {/* Tabs */}
//       <div className="flex justify-center space-x-20 border-b border-gray-700">
//         {tabs.map((tab) => (
//           <button
//             key={tab.key}
//             onClick={() => setActiveTab(tab.key)}
//             className={`relative py-3 text-lg font-medium transition ${
//               activeTab === tab.key
//                 ? "text-blue-400"
//                 : "text-gray-400 hover:text-white"
//             }`}
//           >
//             {tab.label}
//             {activeTab === tab.key && (
//               <span className="absolute left-0 right-0 -bottom-1 h-1 bg-blue-500 rounded-full"></span>
//             )}
//           </button>
//         ))}
//       </div>

//       {/* Content */}
//       <div className="p-6">{renderContent()}</div>
//     </div>
//   );
// }

// export default DashboardPage