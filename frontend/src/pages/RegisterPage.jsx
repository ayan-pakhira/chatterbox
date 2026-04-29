import React, {useState} from 'react'
import { Link } from 'react-router-dom'
import { useNavigate } from 'react-router-dom'
import { registerUserApi } from '../services/PublicApi'
import toast from 'react-hot-toast';
import { useAuth } from '../context/AuthContext.jsx';

function RegisterPage() {

    const [formData, setFormData] = useState({
        userName: "",
        email: "",
        password: ""
    });
    const navigate = useNavigate();

    const {register} = useAuth();

    

    //handling input form
    function handleChange(event){
        setFormData({
            ...formData,
            [event.target.name]: event.target.value
        })
    }


    //form validation
    function validateForm(){
         if(formData.username === "" || formData.email === "" || formData.password === ""){
            toast.error("invalid input");
            return false;
    }

    return true;
    }
       

    //handling register with the register api.
    async function registerApi(){

        if(validateForm()){

            try{

             await register(formData)
              navigate('/');
            
            }catch(error){
                toast.error("Registeration failed. Please try again.");
                console.error("Registration failed:", error);
            }
        }

       
    }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-gray-900 via-black to-gray-800">
      <div className="w-full max-w-md bg-white p-8 rounded-2xl shadow-2xl">
        
        {/* Title */}
        <h2 className="text-3xl font-extrabold text-center mb-8 text-gray-900">
          Create your <span className="text-blue-600">ChatterBox</span> account
        </h2>

        {/* Username */}
        <div className="mb-5">
          <label className="block mb-2 text-sm font-medium text-gray-700">Username</label>
          <input
            onChange={handleChange}
            value={formData.userName}
            name="userName"
            type="text"
            id="name"
            placeholder="Enter your username"
            className="w-full p-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 outline-none"
          />
        </div>

        {/* Email */}
        <div className="mb-5">
          <label className="block mb-2 text-sm font-medium text-gray-700">Email</label>
          <input
            onChange={handleChange}
            value={formData.email}
            name="email"
            type="email"
            id="email"
            placeholder="Enter your email"
            className="w-full p-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 outline-none"
          />
        </div>

        {/* Password */}
        <div className="mb-5">
          <label className="block mb-2 text-sm font-medium text-gray-700">Password</label>
          <input
            onChange={handleChange}
            value={formData.password}
            name="password"
            type="text"
            id="password"
            placeholder="Enter your password"
            className="w-full p-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 outline-none"
          />
        </div>

        {/* Sign Up Button */}
        <button 
        onClick={registerApi}
        className="w-full py-3 bg-blue-600 text-white rounded-xl font-semibold shadow-md hover:bg-blue-700 transition">
          Sign Up
        </button>

        {/* OR Divider */}
        <div className="flex items-center my-6">
          <hr className="flex-grow border-gray-300" />
          <span className="mx-3 text-gray-500">OR</span>
          <hr className="flex-grow border-gray-300" />
        </div>

        {/* Google OAuth Box */}
        <button className="w-full py-3 bg-white border border-gray-300 rounded-xl flex items-center justify-center gap-2 shadow-sm hover:bg-gray-100 transition">
          <img
            src="https://www.svgrepo.com/show/475656/google-color.svg"
            alt="Google"
            className="w-6 h-6"
          />
          <span className="text-gray-700">Sign up with Google</span>
        </button>

        {/* Login Redirect */}
        <p className="mt-6 text-center text-gray-600">
          Already have an account?{" "}
          <a href="/login" className="text-blue-600 font-semibold hover:underline">
            Login
          </a>
        </p>
      </div>
    </div>
  )
}

export default RegisterPage