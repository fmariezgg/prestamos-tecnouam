import EstadoBadge from './EstadoBadge';
import { EmptyMessage } from './StateMessages';

export default function PrestamoTable({ prestamos, isAdmin, onDevolver }) {
    if (prestamos.length === 0) {
        return <EmptyMessage>No hay préstamos registrados.</EmptyMessage>;
    }
    return (
        <div className="table-responsive">
            <table className="table table-striped align-middle">
                <thead>
                    <tr>
                        <th>Equipo</th>
                        <th>Solicitante</th>
                        <th>Fecha préstamo</th>
                        <th>Devolución prevista</th>
                        <th>Devolución real</th>
                        <th>Estado</th>
                        {isAdmin && <th>Acciones</th>}
                    </tr>
                </thead>
                <tbody>
                    {prestamos.map((p) => (
                        <tr key={p.id}>
                            <td>{p.equipo.codigoPatrimonial} - {p.equipo.nombre}</td>
                            <td>{p.nombreSolicitante}</td>
                            <td>{p.fechaPrestamo}</td>
                            <td>{p.fechaPrevistaDevolucion}</td>
                            <td>{p.fechaDevolucionReal ?? '-'}</td>
                            <td><EstadoBadge estado={p.estado} /></td>
                            {isAdmin && (
                                <td>
                                    {p.estado === 'ACTIVO' && (
                                        <button className="btn btn-sm btn-success" onClick={() => onDevolver(p)}>Devolver</button>
                                    )}
                                </td>
                            )}
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}
