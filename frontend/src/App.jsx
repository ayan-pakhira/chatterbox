import React from 'react'
import LandingPage from './pages/LandingPage.jsx'
import Dashboard from './wrappers/DashboardWrapper.jsx'
import LoginPage from './pages/LoginPage.jsx'
import RegisterPage from './pages/RegisterPage.jsx'
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import './index.css'
import {Toaster} from 'react-hot-toast';
import ProtectedRoute from "./components/ProtectedRoute";


function App() {
  

  return (
    <>
      <Router>
        <Routes>
          <Route path="/" element={<LandingPage />} />
          <Route path="/dashboard" element={
            <ProtectedRoute>
              <Dashboard />
            </ProtectedRoute>} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
        </Routes>
        <Toaster position="top-right" reverseOrder={false} />
      </Router>
    </>
  )
}

export default App
