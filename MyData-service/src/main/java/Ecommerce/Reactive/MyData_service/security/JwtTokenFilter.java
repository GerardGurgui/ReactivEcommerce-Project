package Ecommerce.Reactive.MyData_service.security;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class JwtTokenFilter implements Filter {


    //REVISAR! CLASE NECESARIA? YA SE REALZIA EN API-GATEWAY PERO NO SE SI LO MISMO

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String authToken = httpServletRequest.getHeader("Authorization");

        // Check if the token is not null and starts with "Bearer " to decode it
        if (authToken != null && authToken.startsWith("Bearer ")) {
            authToken = authToken.substring(7);
            try{
                // Decode the token
                // Check if the token is valid
                // Check if the token is not expired
                // Check if the token is not tampered
                // Check if the token is not revoked
            } catch (Exception e) {
                // If the token is invalid, expired, tampered, or revoked, throw an exception
                throw new ServletException("Invalid or expired token");
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}