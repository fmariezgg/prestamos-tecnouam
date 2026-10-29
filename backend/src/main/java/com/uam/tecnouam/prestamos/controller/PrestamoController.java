package com.uam.tecnouam.prestamos.controller;

import com.uam.tecnouam.prestamos.dto.PrestamoRequest;
import com.uam.tecnouam.prestamos.dto.PrestamoResponse;
import com.uam.tecnouam.prestamos.service.PrestamoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {

    private final PrestamoService service;

    public PrestamoController(PrestamoService service) {
        this.service = service;
    }

    @GetMapping
    public List<PrestamoResponse> listar() {
        return service.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PrestamoResponse registrar(@Valid @RequestBody PrestamoRequest request) {
        return service.registrar(request);
    }

    @PutMapping("/{id}/devolver")
    public PrestamoResponse devolver(@PathVariable Long id) {
        return service.devolver(id);
    }
}
