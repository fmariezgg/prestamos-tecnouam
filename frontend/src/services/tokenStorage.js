// The session lives in sessionStorage: it is cleared when the tab closes.
const KEY = 'tecnouam.session';

export function saveSession(session) {
    sessionStorage.setItem(KEY, JSON.stringify(session));
}

export function loadSession() {
    try {
        return JSON.parse(sessionStorage.getItem(KEY));
    } catch {
        return null;
    }
}

export function clearSession() {
    sessionStorage.removeItem(KEY);
}
