import { useCallback, useEffect, useState } from 'react';
import EquiposPage from './components/EquiposPage';
import LoginForm from './components/LoginForm';
import PrestamosPage from './components/PrestamosPage';
import UserBar from './components/UserBar';
import { setUnauthorizedHandler } from './services/apiClient';
import { getSession, login, logout } from './services/authService';
import { parseError } from './utils/errors';

export default function App() {
    const [session, setSession] = useState(() => getSession());
    const [loginError, setLoginError] = useState(null);
    const [loggingIn, setLoggingIn] = useState(false);
    const [tab, setTab] = useState('equipos');

    // Expired or invalid token (401 from any request): back to the login screen.
    useEffect(() => {
        setUnauthorizedHandler(() => {
            setSession(null);
            setLoginError('Tu sesión expiró o no es válida. Inicia sesión nuevamente.');
        });
    }, []);

    const handleLogin = useCallback(async (username, password) => {
        setLoggingIn(true);
        setLoginError(null);
        try {
            setSession(await login(username, password));
        } catch (e) {
            setLoginError(parseError(e).message);
        } finally {
            setLoggingIn(false);
        }
    }, []);

    const handleLogout = () => {
        logout();
        setSession(null);
        setLoginError(null);
    };

    if (!session) {
        return (
            <div className="container">
                <LoginForm onLogin={handleLogin} error={loginError} loading={loggingIn} />
            </div>
        );
    }

    const isAdmin = session.rol === 'ADMIN';
    return (
        <>
            <UserBar session={session} onLogout={handleLogout} />
            <main className="container pb-5">
                <ul className="nav nav-tabs mb-3">
                    {[['equipos', 'Equipos'], ['prestamos', 'Préstamos']].map(([key, label]) => (
                        <li className="nav-item" key={key}>
                            <button className={`nav-link ${tab === key ? 'active' : ''}`} onClick={() => setTab(key)}>{label}</button>
                        </li>
                    ))}
                </ul>
                {tab === 'equipos' ? <EquiposPage isAdmin={isAdmin} /> : <PrestamosPage isAdmin={isAdmin} />}
            </main>
        </>
    );
}
