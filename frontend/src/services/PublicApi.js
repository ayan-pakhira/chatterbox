import httpClient from "../config/AxiosHelper.js";


export const registerUserApi = async (credentials) => {
    const response = await httpClient.post(`/public/register-user`, credentials, {
        headers:{
            "Content-Type": "application/json"
        }
    });

    return response.data;
}


export const loginUserApi = async (credentials) => {
   const response =  await httpClient.post(`/public/api/login`, credentials, {
        headers: {
            "Content-Type": "application/json"
        }
    });

    return response.data;
}

export const logoutUserApi = async () => {
    const response = await httpClient.post(`/public/api/logout`, {
        headers: {
            "Content-Type": "application/json"
        }
    });

    return response.data;
}