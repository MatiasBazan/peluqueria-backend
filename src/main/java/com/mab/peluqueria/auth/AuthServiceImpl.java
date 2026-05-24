package com.mab.peluqueria.auth;

import com.mab.peluqueria.config.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public LoginResponse login(LoginRequest req) {
        Usuario u = usuarioRepo.findByEmail(req.email())
                .orElseThrow(() -> new BadCredentialsException("Credenciales invalidas"));
        if (!passwordEncoder.matches(req.password(), u.getPassword())) {
            throw new BadCredentialsException("Credenciales invalidas");
        }
        String token = jwtService.generarToken(u);
        return LoginResponse.bearer(token, jwtService.getExpiracionMs(), u);
    }
}
