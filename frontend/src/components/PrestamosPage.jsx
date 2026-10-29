import useFetch from '../hooks/useFetch';
import { listarEquipos } from '../services/equipoService';
import { devolverPrestamo, listarPrestamos, registrarPrestamo } from '../services/prestamoService';
import { parseError } from '../utils/errors';
import PrestamoForm from './PrestamoForm';
import PrestamoTable from './PrestamoTable';
import { ErrorAlert, LoadingSpinner } from './StateMessages';

// Loans plus equipos (the form needs the DISPONIBLE ones).
const cargarTodo = async () => {
    const [prestamos, equipos] = await Promise.all([listarPrestamos(), listarEquipos()]);
    return { prestamos, equipos };
};

export default function PrestamosPage({ isAdmin }) {
    const { data, loading, error, setError, reload } = useFetch(cargarTodo);
    const prestamos = data?.prestamos ?? [];
    const equipos = data?.equipos ?? [];

    const mutar = async (accion) => {
        try {
            await accion();
            setError(null);
            reload({ silent: true });
            return true;
        } catch (e) {
            setError(parseError(e));
            return false;
        }
    };

    return (
        <section>
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h2 className="h5 mb-0">Préstamos</h2>
                <button className="btn btn-outline-secondary btn-sm" onClick={() => reload()} disabled={loading}>Actualizar</button>
            </div>
            <ErrorAlert message={error?.message} onClose={() => setError(null)} fieldErrors={error?.fieldErrors} />
            {isAdmin && (
                <PrestamoForm equipos={equipos} fieldErrors={error?.fieldErrors}
                    onSubmit={(datos) => mutar(() => registrarPrestamo(datos))} />
            )}
            {loading ? <LoadingSpinner label="Cargando préstamos..." />
                : <PrestamoTable prestamos={prestamos} isAdmin={isAdmin}
                    onDevolver={(p) => mutar(() => devolverPrestamo(p.id))} />}
        </section>
    );
}
