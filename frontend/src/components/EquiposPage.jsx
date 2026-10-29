import { useState } from 'react';
import useFetch from '../hooks/useFetch';
import { actualizarEquipo, crearEquipo, eliminarEquipo, listarEquipos } from '../services/equipoService';
import { parseError } from '../utils/errors';
import EquipoForm from './EquipoForm';
import EquipoTable from './EquipoTable';
import { ErrorAlert, LoadingSpinner } from './StateMessages';

// Container: owns the data and the API calls; children only render.
export default function EquiposPage({ isAdmin }) {
    const { data, loading, error, setError, reload } = useFetch(listarEquipos);
    const [editing, setEditing] = useState(null);
    const equipos = data ?? [];

    // Runs a mutation, then reloads from the API. Returns true on success.
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

    const guardar = (datos) =>
        mutar(async () => {
            if (editing) {
                await actualizarEquipo(editing.id, datos);
                setEditing(null);
            } else {
                await crearEquipo(datos);
            }
        });

    const eliminar = (equipo) => {
        if (window.confirm(`¿Eliminar el equipo ${equipo.codigoPatrimonial}?`)) {
            mutar(() => eliminarEquipo(equipo.id));
        }
    };

    return (
        <section>
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h2 className="h5 mb-0">Equipos</h2>
                <button className="btn btn-outline-secondary btn-sm" onClick={() => reload()} disabled={loading}>Actualizar</button>
            </div>
            <ErrorAlert message={error?.message} onClose={() => setError(null)}
                fieldErrors={error?.fieldErrors} />
            {isAdmin && (
                <EquipoForm key={editing?.id ?? 'new'} editing={editing} onSubmit={guardar}
                    onCancel={() => setEditing(null)} fieldErrors={error?.fieldErrors} />
            )}
            {loading ? <LoadingSpinner label="Cargando equipos..." />
                : <EquipoTable equipos={equipos} isAdmin={isAdmin} onEdit={setEditing} onDelete={eliminar} />}
        </section>
    );
}
