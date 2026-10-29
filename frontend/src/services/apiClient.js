import axios from 'axios';
import { clearSession, loadSession } from './tokenStorage';

const apiClient = axios.create({
    baseURL: import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api',
    headers: { 'Content-Type': 'application/json' },
});

let onUnauthorized = () => {};

// App registers the callback that sends the user back to the login screen.
export function setUnauthorizedHandler(handler) {
    onUnauthorized = handler;
}

apiClient.interceptors.request.use((config) => {
    const session = loadSession();
    if (session?.token) {
        config.headers.Authorization = `Bearer ${session.token}`;
    }
    return config;
});

apiClient.interceptors.response.use(
    (response) => response,
    (error) => {
        // A 401 on /auth/login just means wrong credentials, not an expired session.
        const isLogin = error.config?.url?.includes('/auth/login');
        if (error.response?.status === 401 && !isLogin) {
            clearSession();
            onUnauthorized();
        }
        return Promise.reject(error);
    },
);

export default apiClient;
