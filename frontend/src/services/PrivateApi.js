import axios from 'axios'
import httpClient from "../config/AxiosHelper.js";

export const baseURL = "http://localhost:8080";

const privateApi = axios.create({
    baseURL:baseURL,
    headers:{
        "Content-Type":"application/json"
    },
    withCredentials: true
})


privateApi.interceptors.request.use(
    (config) => {
        const user = JSON.parse(localStorage.getItem("user"))
        const token = user?.token;
        if(token){
            
            config.headers.Authorization = `Bearer ${token}`
        }

        return config
    },
    (error) => Promise.reject(error)
);

privateApi.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;

        if(error.response?.status === 401 && !originalRequest._retry){
            originalRequest._retry = true;

            try{
                const response = await httpClient.post(`/public/generate-access-token`,
                    {},
                    {withCredentials: true}
                );

                const newToken = response.data.accessToken;
                localStorage.setItem("token", newToken);

                privateApi.defaults.headers.Authorization = `Bearer ${newToken}`;
                originalRequest.headers.Authorization = `Bearer ${newToken}`;
                return privateApi(originalRequest);

            }catch(error){
                console.log("error in refreshing token ", error);
                return Promise.reject(error);
            }
        }
    }
)

export default privateApi;