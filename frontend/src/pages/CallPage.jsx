import React, { useState } from "react";
import { Phone, Video } from "lucide-react";

function CallPage() {
  // Dummy call history
  const [calls] = useState([
    { id: 1, name: "Alice",   type: "voice", time: "Today, 10:30 AM" },
    { id: 2, name: "Bob",     type: "video", time: "Yesterday, 8:15 PM" },
    { id: 3, name: "Charlie", type: "voice", time: "Yesterday, 3:45 PM" },
    { id: 4, name: "Daisy",   type: "video", time: "Monday, 5:20 PM" },
    { id: 5, name: "Eve",     type: "voice", time: "Sunday, 11:00 AM" },
  ]);

  return (
    <div className="h-screen bg-gradient-to-br from-gray-900 via-teal-900 to-black flex flex-col p-6">
      {/* ---------- Header ---------- */}
      <header className="mb-6">
        <h1 className="text-3xl font-bold text-white">Calls</h1>
        <p className="text-gray-300 text-sm">Your recent call history</p>
      </header>

      {/* ---------- Calls List ---------- */}
      <div className="flex-1 overflow-y-auto space-y-4">
        {calls.map((call) => (
          <div
            key={call.id}
            className="flex items-center justify-between bg-gray-800/70 hover:bg-gray-700/70
                       rounded-2xl px-4 py-3 shadow transition cursor-pointer"
          >
            {/* Left: Avatar + Name + Time */}
            <div className="flex items-center gap-4">
              {/* Simple avatar with first letter */}
              <div className="w-12 h-12 flex items-center justify-center rounded-full 
                              bg-gradient-to-r from-teal-600 to-cyan-600 text-white text-lg font-bold">
                {call.name[0]}
              </div>
              <div>
                <p className="text-white font-semibold">{call.name}</p>
                <p className="text-gray-400 text-sm">{call.time}</p>
              </div>
            </div>

            {/* Right: Call type icon to redial */}
            <button
              className={`p-3 rounded-full transition ${
                call.type === "voice"
                  ? "bg-teal-700 hover:bg-teal-600"
                  : "bg-cyan-700 hover:bg-cyan-600"
              }`}
              title={call.type === "voice" ? "Voice call" : "Video call"}
            >
              {call.type === "voice" ? (
                <Phone className="w-5 h-5 text-white" />
              ) : (
                <Video className="w-5 h-5 text-white" />
              )}
            </button>
          </div>
        ))}
      </div>
    </div>
  );
}

export default CallPage;
