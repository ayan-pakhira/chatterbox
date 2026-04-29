import React from 'react'
import { Link, NavLink } from 'react-router-dom'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx';
import toast from 'react-hot-toast';



function LandingPage() {


  //--todo-- 
  //make the logical change for login button and replace it with the first letter of the username when logged in.

  const {user, isAuthenticated, logout} = useAuth();
  const navigate = useNavigate();

  const userInitial = user?.userName ? user.userName.charAt(0).toUpperCase() : "";

  const handleLogin = () => {
    navigate('/login');
  }

  const handleLogout = () => {
    logout();
    navigate('/');
  }

  const handleRegister = () => {
    navigate('/register')
  }

  const handleDashboard = () => {
    navigate('/dashboard')
  }

  return (
    <div className="relative min-h-screen bg-gradient-to-br from-gray-900 via-black to-gray-800 text-white flex flex-col overflow-hidden">
     

      {/* Navbar */}
      <NavLink
      className="flex items-center justify-between px-10 py-6 shadow-md">
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
        ):(
          <Link
          to="/login"
          className="px-5 py-2 bg-blue-600 rounded-xl shadow-lg hover:shadow-blue-500/50 hover:scale-105 transition-transform">
          Login
        </Link>
        )}
        
      </NavLink>

      {/* Hero Section */}
      <div className="flex-1 flex items-center justify-center px-10">
        <div className="text-right">
          <h2 className="text-6xl md:text-8xl font-extrabold mb-6 drop-shadow-[0_5px_15px_rgba(0,0,0,0.7)] text-transparent bg-clip-text bg-gradient-to-r from-blue-400 to-purple-500">
            ChatterBox
          </h2>
        
         
          <button
          onClick={isAuthenticated ? (handleDashboard) : (handleRegister)} 
          className="mt-4 px-8 py-3 bg-gradient-to-r from-blue-500 to-purple-600 rounded-2xl font-semibold shadow-xl hover:shadow-purple-500/50 hover:scale-105 transition-transform">
            Get Started
          </button>
          
          
        </div>
      </div>

       <footer className="w-full py-4 text-center text-white/50 text-sm border-t border-white/10">
        © {new Date().getFullYear()} ChatterBox. All rights reserved.
      </footer>
    </div>
  )
}



export default LandingPage