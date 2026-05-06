package com.example.awsMachineLearningExam.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// 🚨 @Component is the badge that tells Spring Boot this file exists!
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected boolean shouldNotFilter(jakarta.servlet.http.HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/api/auth/login") || path.startsWith("/api/users/register");
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        String username = null; // 🚨 Un-finalized so we can safely catch errors

        // 1. If there is no VIP pass in the header, let the request pass through
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extract the token from the "Bearer " string
        jwt = authHeader.substring(7);

        // 🚨 THE FIX 1: Catch the Vue template literal trap!
        if (jwt.equals("null") || jwt.trim().isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        // 🚨 THE FIX 2: Try-Catch block to prevent server 500 crashes on bad tokens
        try {
            // 3. Hand the token to your JwtService to get the username
            username = jwtService.extractUsername(jwt);
        } catch (Exception e) {
            System.out.println("⚠️ JWT Bouncer intercepted a mangled token. Access Denied.");
            filterChain.doFilter(request, response);
            return;
        }

        // 4. If we found a username, and they aren't already logged into this session...
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Grab the user's full details from the database
            org.springframework.security.core.userdetails.UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 5. Ask JwtService if the token is completely valid
            if (jwtService.validateToken(jwt, userDetails.getUsername())) {

                // 6. OPEN THE DOOR! Create the official Spring Security access token
                org.springframework.security.authentication.UsernamePasswordAuthenticationToken authToken = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(
                        new org.springframework.security.web.authentication.WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Officially log the user in for this specific request
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continue to the next step (like your CSV Controller!)
        filterChain.doFilter(request, response);
    }
}