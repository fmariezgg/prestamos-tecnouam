export default function UserBar({ session, onLogout }) {
    return (
        <nav className="navbar navbar-dark bg-dark mb-4">
            <div className="container">
                <span className="navbar-brand">Préstamos TecnoUAM</span>
                <div className="d-flex align-items-center gap-3 text-white">
                    <span>
                        {session.username} <span className="badge bg-info text-dark">{session.rol}</span>
                    </span>
                    <button className="btn btn-outline-light btn-sm" onClick={onLogout}>Cerrar sesión</button>
                </div>
            </div>
        </nav>
    );
}
