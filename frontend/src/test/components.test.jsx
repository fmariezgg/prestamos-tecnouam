import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import App from '../App';
import EquiposPage from '../components/EquiposPage';
import * as equipoService from '../services/equipoService';
import * as authService from '../services/authService';

vi.mock('../services/equipoService');
vi.mock('../services/authService', async (importOriginal) => ({
    ...(await importOriginal()),
    login: vi.fn(),
}));

beforeEach(() => {
    vi.resetAllMocks();
    sessionStorage.clear();
});

describe('EquiposPage', () => {
    it('shows the loading state while the request is pending', () => {
        equipoService.listarEquipos.mockReturnValue(new Promise(() => {}));
        render(<EquiposPage isAdmin={false} />);
        expect(screen.getByText('Cargando equipos...')).toBeInTheDocument();
    });

    it('shows the empty message when the API returns no equipos', async () => {
        equipoService.listarEquipos.mockResolvedValue([]);
        render(<EquiposPage isAdmin={false} />);
        expect(await screen.findByText('No hay equipos registrados.')).toBeInTheDocument();
    });

    it('hides admin controls for USER', async () => {
        equipoService.listarEquipos.mockResolvedValue([
            { id: 1, codigoPatrimonial: 'PC-1', nombre: 'Laptop', tipo: 'Laptop', estado: 'DISPONIBLE' },
        ]);
        render(<EquiposPage isAdmin={false} />);
        expect(await screen.findByText('PC-1')).toBeInTheDocument();
        expect(screen.queryByText('Eliminar')).not.toBeInTheDocument();
        expect(screen.queryByText('Nuevo equipo')).not.toBeInTheDocument();
    });

    it('shows an error alert when the request fails', async () => {
        equipoService.listarEquipos.mockRejectedValue({ response: { status: 500, data: { message: 'Fallo interno' } } });
        render(<EquiposPage isAdmin={false} />);
        expect(await screen.findByText('Fallo interno')).toBeInTheDocument();
    });
});

describe('App login', () => {
    it('shows the backend error when credentials are wrong', async () => {
        authService.login.mockRejectedValue({ response: { status: 401, data: { message: 'Credenciales inválidas' } } });
        render(<App />);
        await userEvent.type(screen.getByLabelText('Usuario'), 'admin');
        await userEvent.type(screen.getByLabelText('Contraseña'), 'mala');
        await userEvent.click(screen.getByRole('button', { name: 'Ingresar' }));
        await waitFor(() => expect(screen.getByRole('alert')).toHaveTextContent('Credenciales inválidas'));
    });
});
