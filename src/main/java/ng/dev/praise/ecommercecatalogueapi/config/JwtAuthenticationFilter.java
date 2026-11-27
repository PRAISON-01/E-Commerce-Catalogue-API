package ng.dev.praise.ecommercecatalogueapi.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

//    private final String SECRET_STRING = "your-super-secret-hex-signing-key-must-be-at-least-256-bits-long-praise-dev-2026";
    @Value("$(app.jwt.secret)")
    private String SECRET_STRING;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_STRING.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String email = claims.getSubject();
            String role = claims.get("role", String.class);
            String status = claims.get("status", String.class);

            if (email != null && "ACTIVE".equals(status) && SecurityContextHolder.getContext().getAuthentication() == null) {
                List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
