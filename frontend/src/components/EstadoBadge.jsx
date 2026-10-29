const COLORS = { DISPONIBLE: 'success', PRESTADO: 'warning', ACTIVO: 'warning', DEVUELTO: 'success' };

export default function EstadoBadge({ estado }) {
    const color = COLORS[estado] ?? 'secondary';
    return <span className={`badge bg-${color} ${color === 'warning' ? 'text-dark' : ''}`}>{estado}</span>;
}
