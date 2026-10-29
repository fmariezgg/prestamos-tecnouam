import apiClient from './apiClient';

export const listarPrestamos = () => apiClient.get('/prestamos').then((r) => r.data);
export const registrarPrestamo = (prestamo) => apiClient.post('/prestamos', prestamo).then((r) => r.data);
export const devolverPrestamo = (id) => apiClient.put(`/prestamos/${id}/devolver`).then((r) => r.data);
