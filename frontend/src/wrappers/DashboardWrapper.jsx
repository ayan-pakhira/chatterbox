import React from "react";
import { ChatProvider } from "../context/ChatContext.jsx";
import { MessageProvider } from "../context/MessageContext.jsx";
import DashboardPage from "../pages/DashboardPage.jsx";

export default function DashboardWrapper() {
  return (
    <ChatProvider>
      <MessageProvider>
        <DashboardPage />
      </MessageProvider>
    </ChatProvider>
  );
}
