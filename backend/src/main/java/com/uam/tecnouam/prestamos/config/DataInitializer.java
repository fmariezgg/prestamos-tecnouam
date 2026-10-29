package com.uam.tecnouam.prestamos.config;

import com.uam.tecnouam.prestamos.model.Rol;
import com.uam.tecnouam.prestamos.model.Usuario;
import com.uam.tecnouam.prestamos.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Creates the two application accounts at startup if they do not exist yet. */
@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminPassword;
    private final String userPassword;

    public DataInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                           @Value("${app.users.admin-password:}") String adminPassword,
                           @Value("${app.users.user-password:}") String userPassword) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminPassword = adminPassword;
        this.userPassword = userPassword;
    }

    @Override
    public void run(ApplicationArguments args) {
        crearSiNoExiste("admin", adminPassword, Rol.ADMIN, "ADMIN_PASSWORD");
        crearSiNoExiste("user", userPassword, Rol.USER, "USER_PASSWORD");
    }

    private void crearSiNoExiste(String username, String password, Rol rol, String envName) {
        if (usuarioRepository.existsByUsername(username)) {
            return;
        }
        if (password.isBlank()) {
            throw new IllegalStateException(envName + " must be set to create the '" + username + "' account");
        }
        usuarioRepository.save(new Usuario(username, passwordEncoder.encode(password), rol));
        log.info("Created account '{}' with role {}", username, rol);
    }
}
