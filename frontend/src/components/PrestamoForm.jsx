import { useState } from 'react';

const today = () => new Date().toISOString().slice(0, 10);

// Only DISPONIBLE equipos can be selected; the backend enforces it as well.
export default function PrestamoForm({ equipos, onSubmit, fieldErrors = {} }) {
    const disponibles = equipos.filter((e) => e.estado === 'DISPONIBLE');
    const [form, setForm] = useState({ equipoId: '', nombreSolicitante: '', fechaPrestamo: today(), fechaPrevistaDevolucion: '' });

    const handleChange = (event) => setForm({ ...form, [event.target.name]: event.target.value });

    const handleSubmit = async (event) => {
        event.preventDefault();
        const saved = await onSubmit({ ...form, equipoId: Number(form.equipoId) });
        if (saved) setForm({ ...form, equipoId: '', nombreSolicitante: '', fechaPrevistaDevolucion: '' });
    };

    const invalid = (name) => (fieldErrors[name] ? 'is-invalid' : '');
    const feedback = (name) => fieldErrors[name] && <div className="invalid-feedback">{fieldErrors[name]}</div>;

    return (
        <form className="card card-body mb-3" onSubmit={handleSubmit}>
            <h2 className="h6">Registrar préstamo</h2>
            <div className="row g-2 align-items-start">
                <div className="col-md-3">
                    <label htmlFor="prestamo-equipoId" className="form-label">Equipo disponible</label>
                    <select id="prestamo-equipoId" name="equipoId" value={form.equipoId} onChange={handleChange}
                        className={`form-select ${invalid('equipoId')}`} required>
                        <option value="">Seleccione...</option>
                        {disponibles.map((e) => (
                            <option key={e.id} value={e.id}>{e.codigoPatrimonial} - {e.nombre}</option>
                        ))}
                    </select>
                    {feedback('equipoId')}
                </div>
                <div className="col-md-3">
                    <label htmlFor="prestamo-solicitante" className="form-label">Solicitante</label>
                    <input id="prestamo-solicitante" name="nombreSolicitante" value={form.nombreSolicitante}
                        onChange={handleChange} className={`form-control ${invalid('nombreSolicitante')}`} required />
                    {feedback('nombreSolicitante')}
                </div>
                <div className="col-md-2">
                    <label htmlFor="prestamo-fecha" className="form-label">Fecha préstamo</label>
                    <input id="prestamo-fecha" type="date" name="fechaPrestamo" value={form.fechaPrestamo}
                        onChange={handleChange} className={`form-control ${invalid('fechaPrestamo')}`} required />
                    {feedback('fechaPrestamo')}
                </div>
                <div className="col-md-2">
                    <label htmlFor="prestamo-prevista" className="form-label">Devolución prevista</label>
                    <input id="prestamo-prevista" type="date" name="fechaPrevistaDevolucion" value={form.fechaPrevistaDevolucion}
                        onChange={handleChange} className={`form-control ${invalid('fechaPrevistaDevolucion')}`} required />
                    {feedback('fechaPrevistaDevolucion')}
                </div>
                <div className="col-md-2 d-flex align-items-end" style={{ minHeight: '4.5rem' }}>
                    <button type="submit" className="btn btn-primary">Registrar</button>
                </div>
            </div>
        </form>
    );
}
