package com.uam.tecnouam.prestamos.service;

import com.uam.tecnouam.prestamos.dto.LoginRequest;
import com.uam.tecnouam.prestamos.dto.LoginResponse;
import com.uam.tecnouam.prestamos.exception.CredencialesInvalidasException;
import com.uam.tecnouam.prestamos.model.Usuario;
import com.uam.tecnouam.prestamos.repository.UsuarioRepository;
import com.uam.tecnouam.prestamos.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        // Same error for unknown user and wrong password: do not reveal which one failed.
        Usuario usuario = usuarioRepository.findByUsername(request.username())
                .filter(u -> passwordEncoder.matches(request.password(), u.getPasswordHash()))
                .orElseThrow(CredencialesInvalidasException::new);
        String token = jwtService.generarToken(usuario.getUsername(), usuario.getRol());
        return new LoginResponse(token, "Bearer", jwtService.expiracionSegundos(), usuario.getUsername(),
                usuario.getRol());
    }
}
