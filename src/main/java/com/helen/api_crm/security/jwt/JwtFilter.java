package com.helen.api_crm.security.jwt;

import com.helen.api_crm.security.model.SecurityUser;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            jwt = authHeader.substring(7);
            userEmail = jwtService.extractUsername(jwt);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                Long userId = jwtService.extractUserId(jwt);
                String role = jwtService.extractRole(jwt);

                SecurityUser userDetails = new SecurityUser(userId, userEmail, role);

                if (jwtService.isTokenValid(jwt, userEmail)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    log.debug("User authenticated via JWT: {} (ID: {}", userEmail, userId);
                }
            }
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            log.warn("JWT Expired: {}", e.getMessage());
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Token expired");
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT Error: {}", e.getMessage());
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid token");
        } catch (Exception e) {
            log.error("Authentication Error: {}", e.getMessage());
            sendErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "Authentication error");
        }
    }

        private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
            response.setStatus(status.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            String jsonError = String.format("{\"status\": %d, \"error\": \"%s\", \"message\": \"%s\"}",
                status.value(), status.getReasonPhrase(), message);
            response.getWriter().write(jsonError);
        }
    }

