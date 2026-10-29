import { useState } from 'react';

const EMPTY = { codigoPatrimonial: '', nombre: '', tipo: '' };

// `editing` is the equipo being edited, or null to create a new one.
export default function EquipoForm({ editing, onSubmit, onCancel, fieldErrors = {} }) {
    const [form, setForm] = useState(
        editing ? { codigoPatrimonial: editing.codigoPatrimonial, nombre: editing.nombre, tipo: editing.tipo } : EMPTY,
    );

    const handleChange = (event) => setForm({ ...form, [event.target.name]: event.target.value });

    const handleSubmit = async (event) => {
        event.preventDefault();
        const saved = await onSubmit(form);
        if (saved && !editing) setForm(EMPTY);
    };

    const field = (name, label) => (
        <div className="col-md-3">
            <label htmlFor={`equipo-${name}`} className="form-label">{label}</label>
            <input id={`equipo-${name}`} name={name} value={form[name]} onChange={handleChange}
                className={`form-control ${fieldErrors[name] ? 'is-invalid' : ''}`} />
            {fieldErrors[name] && <div className="invalid-feedback">{fieldErrors[name]}</div>}
        </div>
    );

    return (
        <form className="card card-body mb-3" onSubmit={handleSubmit}>
            <h2 className="h6">{editing ? `Editar equipo ${editing.codigoPatrimonial}` : 'Nuevo equipo'}</h2>
            <div className="row g-2 align-items-start">
                {field('codigoPatrimonial', 'Código patrimonial')}
                {field('nombre', 'Nombre')}
                {field('tipo', 'Tipo')}
                <div className="col-md-3 d-flex gap-2 align-items-end" style={{ minHeight: '4.5rem' }}>
                    <button type="submit" className="btn btn-primary">{editing ? 'Guardar' : 'Crear'}</button>
                    {editing && <button type="button" className="btn btn-secondary" onClick={onCancel}>Cancelar</button>}
                </div>
            </div>
        </form>
    );
}
