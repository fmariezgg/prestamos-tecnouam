export function LoadingSpinner({ label = 'Cargando...' }) {
    return (
        <div className="text-center py-4" role="status">
            <div className="spinner-border text-primary" aria-hidden="true"></div>
            <div className="mt-2">{label}</div>
        </div>
    );
}

export function EmptyMessage({ children }) {
    return <div className="alert alert-secondary">{children}</div>;
}

export function ErrorAlert({ message, fieldErrors = {}, onClose }) {
    if (!message) return null;
    const entries = Object.entries(fieldErrors);
    return (
        <div className="alert alert-danger alert-dismissible" role="alert">
            {message}
            {entries.length > 0 && (
                <ul className="mb-0 mt-1">
                    {entries.map(([field, text]) => (
                        <li key={field}>
                            <strong>{field}</strong>: {text}
                        </li>
                    ))}
                </ul>
            )}
            {onClose && <button type="button" className="btn-close" aria-label="Cerrar" onClick={onClose}></button>}
        </div>
    );
}
