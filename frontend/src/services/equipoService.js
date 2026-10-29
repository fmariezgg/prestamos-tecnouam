import apiClient from './apiClient';

export const listarEquipos = () => apiClient.get('/equipos').then((r) => r.data);
export const crearEquipo = (equipo) => apiClient.post('/equipos', equipo).then((r) => r.data);
export const actualizarEquipo = (id, equipo) => apiClient.put(`/equipos/${id}`, equipo).then((r) => r.data);
export const eliminarEquipo = (id) => apiClient.delete(`/equipos/${id}`);
