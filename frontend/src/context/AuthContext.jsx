import React, {useState, useEffect, useContext} from 'react'
import { createContext } from 'react'
import toast from 'react-hot-toast';
import { useNavigate } from 'react-router-dom';
import { registerUserApi, loginUserApi, logoutUserApi } from '../services/PublicApi'
import privateApi from '../services/PrivateApi';

//first step - create the context
const AuthContext = createContext();

//second step - create the provider
export const AuthProvider = ({children}) => {
    const [user, setUser] = useState(null);
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [loading, setLoading] = useState(true);

    // Load user from localStorage if available
useEffect(() => {
  const storedUser = localStorage.getItem('user');
  
  if (storedUser && storedUser !== "undefined") {
    try {
      setUser(JSON.parse(storedUser));
      setIsAuthenticated(true);
    } catch (err) {
      console.error("Failed to parse stored user:", err);
      localStorage.removeItem('user'); // remove invalid entry
    }
  }
  setLoading(false);
}, []);


    //registration function
    const register = async (userData) => {
      const response = await registerUserApi(userData);
      if(!response || response.error){
        toast.error(response?.error || "Registration failed. Please try again.");
      }
      toast.success("Registration successful!");
      console.log("response from register api ", response);
       
      const userWithToken = {
        id: response.id,
        email: response.email,
        userName: response.userName,
        token: response.token
      };
      setUser(userWithToken);
      setIsAuthenticated(true);
      localStorage.setItem('user', JSON.stringify(userWithToken));
      
    }

    //login function
    const login = async (userData) => {

      try{

        const response = await loginUserApi(userData);
        if(!response || response.error){
          toast.error(response?.error || "Login failed. Please try again.");
        
        }
        toast.success("Login successful!");
        console.log("response from login api ", response);

        const userInfo = {
          id: response.id,
          email: response.email,
          token: response.token,
          userName: response.userName
        };

        
        localStorage.setItem('user', JSON.stringify(userInfo));
        //localStorage.setItem("token", response.token);
        setUser(userInfo);
        setIsAuthenticated(true);


      }catch(error){
        console.log("login failed ", error)
        throw error;
      }  

        
    } 

    //logout function
    const logout = async () => {
      const response = await logoutUserApi();
      console.log("response from logout api ", response);
      setUser(null);
      setIsAuthenticated(false);
      localStorage.removeItem('user');
      toast.success("Logged out successfully");
    }

    return (
        <AuthContext.Provider
        value={{user, isAuthenticated, register, login, logout, loading}}>
            {children}
        </AuthContext.Provider>
    )
}

export const useAuth = () => useContext(AuthContext);
