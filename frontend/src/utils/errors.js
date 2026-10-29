// Turns an Axios error into { message, fieldErrors } using the backend ErrorResponse body.
export function parseError(error) {
    const body = error.response?.data;
    if (!error.response) {
        return { message: 'No se pudo conectar con el servidor. Verifica que el backend esté en ejecución.', fieldErrors: {} };
    }
    return {
        message: body?.message ?? `Error inesperado (HTTP ${error.response.status})`,
        fieldErrors: body?.fieldErrors ?? {},
    };
}
