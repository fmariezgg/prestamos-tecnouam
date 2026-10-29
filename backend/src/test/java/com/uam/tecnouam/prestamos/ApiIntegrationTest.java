package com.uam.tecnouam.prestamos;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

/** End-to-end API tests (controllers + security + services + H2). Each test rolls back its changes. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ApiIntegrationTest {

    private static final AtomicInteger SEQ = new AtomicInteger();

    @Autowired
    private MockMvc mvc;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void login() throws Exception {
        adminToken = tokenFor("admin", "test-admin-dummy");
        userToken = tokenFor("user", "test-user-dummy");
    }

    // ---- acceptance matrix ----

    @Test
    void getEquiposWithoutTokenIs401WithJsonBody() throws Exception {
        mvc.perform(get("/api/equipos"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path").value("/api/equipos"));
    }

    @Test
    void invalidTokenIs401() throws Exception {
        mvc.perform(get("/api/equipos").header(HttpHeaders.AUTHORIZATION, "Bearer not.a.token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void getEquiposWithUserTokenIs200() throws Exception {
        mvc.perform(get("/api/equipos").header(HttpHeaders.AUTHORIZATION, bearer(userToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigoPatrimonial").isNotEmpty());
    }

    @Test
    void postEquipoWithUserTokenIs403WithJsonBody() throws Exception {
        postJson("/api/equipos", userToken, equipoJson(uniqueCode(), "Laptop X", "LAPTOP"))
                .andExpect(status().isForbidden())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"));
    }

    @Test
    void postEquipoWithAdminAndValidDataIs201() throws Exception {
        postJson("/api/equipos", adminToken, equipoJson(uniqueCode(), "Laptop X", "LAPTOP"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.estado").value("DISPONIBLE"));
    }

    @Test
    void loginWithWrongCredentialsIs401() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Usuario o contraseña incorrectos"));
    }

    @Test
    void loginReturnsTokenAndRole() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"test-admin-dummy\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(notNullValue()))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(3600))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.rol").value("ADMIN"));
    }

    // ---- equipos CRUD ----

    @Test
    void getEquipoByIdAndUpdate() throws Exception {
        long id = createEquipo(uniqueCode());
        mvc.perform(get("/api/equipos/" + id).header(HttpHeaders.AUTHORIZATION, bearer(userToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));

        String code = uniqueCode();
        putJson("/api/equipos/" + id, adminToken, equipoJson(code, "Renombrado", "TABLETA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoPatrimonial").value(code))
                .andExpect(jsonPath("$.nombre").value("Renombrado"));
    }

    @Test
    void userCannotUpdateOrDelete() throws Exception {
        long id = createEquipo(uniqueCode());
        putJson("/api/equipos/" + id, userToken, equipoJson(uniqueCode(), "X", "LAPTOP"))
                .andExpect(status().isForbidden());
        mvc.perform(delete("/api/equipos/" + id).header(HttpHeaders.AUTHORIZATION, bearer(userToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    void duplicatedCodigoPatrimonialIs409() throws Exception {
        String code = uniqueCode();
        createEquipo(code);
        postJson("/api/equipos", adminToken, equipoJson(code, "Otro", "LAPTOP"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void missingEquipoIs404() throws Exception {
        mvc.perform(get("/api/equipos/999999").header(HttpHeaders.AUTHORIZATION, bearer(userToken)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.path").value("/api/equipos/999999"));
    }

    @Test
    void invalidEquipoBodyIs400WithFieldErrors() throws Exception {
        postJson("/api/equipos", adminToken, "{\"codigoPatrimonial\":\"\",\"nombre\":\"\",\"tipo\":null}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors.codigoPatrimonial").isNotEmpty())
                .andExpect(jsonPath("$.fieldErrors.nombre").isNotEmpty())
                .andExpect(jsonPath("$.fieldErrors.tipo").isNotEmpty());
    }

    @Test
    void malformedJsonIs400() throws Exception {
        postJson("/api/equipos", adminToken, "{not json")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    // ---- prestamos ----

    @Test
    void loanFlipsEquipoToPrestadoAndReturnFlipsBack() throws Exception {
        long equipoId = createEquipo(uniqueCode());

        String body = postJson("/api/prestamos", adminToken, prestamoJson(equipoId, "2026-10-01", "2026-10-10"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("ACTIVO"))
                .andExpect(jsonPath("$.equipo.id").value(equipoId))
                .andReturn().getResponse().getContentAsString();
        int prestamoId = JsonPath.read(body, "$.id");

        mvc.perform(get("/api/equipos/" + equipoId).header(HttpHeaders.AUTHORIZATION, bearer(userToken)))
                .andExpect(jsonPath("$.estado").value("PRESTADO"));

        mvc.perform(put("/api/prestamos/" + prestamoId + "/devolver")
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("DEVUELTO"))
                .andExpect(jsonPath("$.fechaDevolucionReal").isNotEmpty());

        mvc.perform(get("/api/equipos/" + equipoId).header(HttpHeaders.AUTHORIZATION, bearer(userToken)))
                .andExpect(jsonPath("$.estado").value("DISPONIBLE"));
    }

    @Test
    void loanOfAlreadyLoanedEquipoIs409() throws Exception {
        long equipoId = createEquipo(uniqueCode());
        createPrestamo(equipoId);
        postJson("/api/prestamos", adminToken, prestamoJson(equipoId, "2026-10-02", "2026-10-12"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void returningTwiceIs409() throws Exception {
        long prestamoId = createPrestamo(createEquipo(uniqueCode()));
        mvc.perform(put("/api/prestamos/" + prestamoId + "/devolver")
                .header(HttpHeaders.AUTHORIZATION, bearer(adminToken))).andExpect(status().isOk());
        mvc.perform(put("/api/prestamos/" + prestamoId + "/devolver")
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
                .andExpect(status().isConflict());
    }

    @Test
    void returningMissingLoanIs404AndLoanOfMissingEquipoIs404() throws Exception {
        mvc.perform(put("/api/prestamos/999999/devolver").header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
                .andExpect(status().isNotFound());
        postJson("/api/prestamos", adminToken, prestamoJson(999999, "2026-10-01", "2026-10-10"))
                .andExpect(status().isNotFound());
    }

    @Test
    void returnDateBeforeLoanDateIs400() throws Exception {
        long equipoId = createEquipo(uniqueCode());
        postJson("/api/prestamos", adminToken, prestamoJson(equipoId, "2026-10-10", "2026-10-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.fechaPrevistaDevolucion", containsString("anterior")));
    }

    @Test
    void invalidPrestamoBodyIs400() throws Exception {
        postJson("/api/prestamos", adminToken, "{\"nombreSolicitante\":\"\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.equipoId").isNotEmpty())
                .andExpect(jsonPath("$.fieldErrors.nombreSolicitante").isNotEmpty())
                .andExpect(jsonPath("$.fieldErrors.fechaPrestamo").isNotEmpty())
                .andExpect(jsonPath("$.fieldErrors.fechaPrevistaDevolucion").isNotEmpty());
        postJson("/api/prestamos", adminToken,
                "{\"equipoId\":1,\"nombreSolicitante\":\"Ana\",\"fechaPrestamo\":\"no-es-fecha\","
                        + "\"fechaPrevistaDevolucion\":\"2026-10-10\"}")
                .andExpect(status().isBadRequest());
    }

    @Test
    void userCanListPrestamosButCannotCreate() throws Exception {
        mvc.perform(get("/api/prestamos").header(HttpHeaders.AUTHORIZATION, bearer(userToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
        postJson("/api/prestamos", userToken, prestamoJson(1, "2026-10-01", "2026-10-10"))
                .andExpect(status().isForbidden());
        mvc.perform(put("/api/prestamos/1/devolver").header(HttpHeaders.AUTHORIZATION, bearer(userToken)))
                .andExpect(status().isForbidden());
    }

    // ---- delete rules ----

    @Test
    void deleteEquipoWithActiveLoanIs409() throws Exception {
        long equipoId = createEquipo(uniqueCode());
        createPrestamo(equipoId);
        mvc.perform(delete("/api/equipos/" + equipoId).header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void deleteEquipoWithoutLoansIs204AndThenGone() throws Exception {
        long equipoId = createEquipo(uniqueCode());
        mvc.perform(delete("/api/equipos/" + equipoId).header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/equipos/" + equipoId).header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteEquipoWithOnlyReturnedLoansIs204AndRemovesHistory() throws Exception {
        long equipoId = createEquipo(uniqueCode());
        long prestamoId = createPrestamo(equipoId);
        mvc.perform(put("/api/prestamos/" + prestamoId + "/devolver")
                .header(HttpHeaders.AUTHORIZATION, bearer(adminToken))).andExpect(status().isOk());
        mvc.perform(delete("/api/equipos/" + equipoId).header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/prestamos").header(HttpHeaders.AUTHORIZATION, bearer(userToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + prestamoId + ")]").isEmpty());
    }

    // ---- helpers ----

    private String tokenFor(String username, String password) throws Exception {
        String body = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.token");
    }

    private long createEquipo(String code) throws Exception {
        String body = postJson("/api/equipos", adminToken, equipoJson(code, "Equipo " + code, "LAPTOP"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(body, "$.id")).longValue();
    }

    private long createPrestamo(long equipoId) throws Exception {
        String body = postJson("/api/prestamos", adminToken, prestamoJson(equipoId, "2026-10-01", "2026-10-10"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(body, "$.id")).longValue();
    }

    private ResultActions postJson(String url, String token, String json) throws Exception {
        return mvc.perform(post(url).header(HttpHeaders.AUTHORIZATION, bearer(token))
                .contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private ResultActions putJson(String url, String token, String json) throws Exception {
        return mvc.perform(put(url).header(HttpHeaders.AUTHORIZATION, bearer(token))
                .contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private static String bearer(String token) {
        return "Bearer " + token;
    }

    private static String uniqueCode() {
        return "T-" + SEQ.incrementAndGet();
    }

    private static String equipoJson(String code, String nombre, String tipo) {
        return "{\"codigoPatrimonial\":\"" + code + "\",\"nombre\":\"" + nombre + "\",\"tipo\":\"" + tipo + "\"}";
    }

    private static String prestamoJson(long equipoId, String desde, String hasta) {
        return "{\"equipoId\":" + equipoId + ",\"nombreSolicitante\":\"Ana Torres\",\"fechaPrestamo\":\"" + desde
                + "\",\"fechaPrevistaDevolucion\":\"" + hasta + "\"}";
    }
}
