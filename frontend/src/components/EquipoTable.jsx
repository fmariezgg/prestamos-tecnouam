import EstadoBadge from './EstadoBadge';
import { EmptyMessage } from './StateMessages';

export default function EquipoTable({ equipos, isAdmin, onEdit, onDelete }) {
    if (equipos.length === 0) {
        return <EmptyMessage>No hay equipos registrados.</EmptyMessage>;
    }
    return (
        <div className="table-responsive">
            <table className="table table-striped align-middle">
                <thead>
                    <tr>
                        <th>Código patrimonial</th>
                        <th>Nombre</th>
                        <th>Tipo</th>
                        <th>Estado</th>
                        {isAdmin && <th>Acciones</th>}
                    </tr>
                </thead>
                <tbody>
                    {equipos.map((equipo) => (
                        <tr key={equipo.id}>
                            <td>{equipo.codigoPatrimonial}</td>
                            <td>{equipo.nombre}</td>
                            <td>{equipo.tipo}</td>
                            <td><EstadoBadge estado={equipo.estado} /></td>
                            {isAdmin && (
                                <td className="text-nowrap">
                                    <button className="btn btn-sm btn-outline-primary me-2" onClick={() => onEdit(equipo)}>Editar</button>
                                    <button className="btn btn-sm btn-outline-danger" onClick={() => onDelete(equipo)}>Eliminar</button>
                                </td>
                            )}
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}
