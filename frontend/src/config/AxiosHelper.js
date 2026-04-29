import axios from "axios";

export const baseURL = "http://localhost:8080";


const httpClient = axios.create({
    baseURL: baseURL,
    headers: {
    "Content-Type": "application/json",
  },
    //,
    withCredentials: true, // Include cookies in requests
})

export default httpClient;