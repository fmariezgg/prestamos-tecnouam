import apiClient from './apiClient';
import { clearSession, loadSession, saveSession } from './tokenStorage';

export async function login(username, password) {
    const { data } = await apiClient.post('/auth/login', { username, password });
    const session = { token: data.token, username: data.username, rol: data.rol };
    saveSession(session);
    return session;
}

export function logout() {
    clearSession();
}

export function getSession() {
    return loadSession();
}
