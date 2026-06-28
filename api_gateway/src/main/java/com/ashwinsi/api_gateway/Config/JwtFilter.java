package com.ashwinsi.api_gateway.Config;

import com.ashwinsi.api_gateway.DTO.SellerJwtData;
import com.ashwinsi.api_gateway.DTO.UserJwtData;
import com.ashwinsi.api_gateway.Utils.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        if(!jwtService.isValidToken(token)){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Invalid or expired token");
            return;
        }

        String endPoint = request.getRequestURI();
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken;
        if(endPoint.contains("/user")){
            UserJwtData userJwtData = jwtService.parseUserToken(token);
             usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                    userJwtData.getId(), null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
             );

        }else{
            SellerJwtData sellerJwtData = jwtService.parseSellerToken(token);
            if(sellerJwtData.getIsAdmin()){
                usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        sellerJwtData.getId(), null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                );
            }else{
                usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        sellerJwtData.getId(), null, List.of(new SimpleGrantedAuthority("ROLE_SELLER"))
                );
            }
        }

        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

        filterChain.doFilter(request, response);
    }
}
