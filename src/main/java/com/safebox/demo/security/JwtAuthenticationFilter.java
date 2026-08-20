package com.safebox.demo.security;

import com.safebox.demo.entity.User;
import com.safebox.demo.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Étape A — Lire le header Authorization
        String authHeader = request.getHeader("Authorization");

        // Étape B — Si pas de token, on laisse passer sans authentifier
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }


        String token = authHeader.substring(7);

        if (!jwtUtils.isTokenValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Étape D — Charger l'utilisateur depuis la base
        String email = jwtUtils.extractEmail(token);
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Étape E — Dire à Spring que ce user est authentifié
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                user,                                                        // le principal (qui est-ce)
                null,                                                        // pas de credentials (déjà vérifié via JWT)
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())) // les rôles
        );

        SecurityContextHolder.getContext().setAuthentication(authToken);

        // Passer la main au filtre suivant
        filterChain.doFilter(request, response);
    }
}
