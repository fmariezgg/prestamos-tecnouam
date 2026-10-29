import { useState } from 'react';
import { ErrorAlert } from './StateMessages';

export default function LoginForm({ onLogin, error, loading }) {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    const handleSubmit = (event) => {
        event.preventDefault();
        onLogin(username.trim(), password);
    };

    return (
        <div className="row justify-content-center mt-5">
            <div className="col-md-5 col-lg-4">
                <div className="card shadow-sm">
                    <div className="card-body">
                        <h1 className="h4 mb-3">Préstamos TecnoUAM</h1>
                        <ErrorAlert message={error} />
                        <form onSubmit={handleSubmit}>
                            <div className="mb-3">
                                <label htmlFor="username" className="form-label">Usuario</label>
                                <input id="username" className="form-control" value={username} autoComplete="username"
                                    onChange={(e) => setUsername(e.target.value)} required />
                            </div>
                            <div className="mb-3">
                                <label htmlFor="password" className="form-label">Contraseña</label>
                                <input id="password" type="password" className="form-control" value={password}
                                    autoComplete="current-password" onChange={(e) => setPassword(e.target.value)} required />
                            </div>
                            <button type="submit" className="btn btn-primary w-100" disabled={loading}>
                                {loading ? 'Ingresando...' : 'Ingresar'}
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    );
}
