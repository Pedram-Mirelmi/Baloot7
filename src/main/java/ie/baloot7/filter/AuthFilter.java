package ie.baloot7.filter;

import static ie.baloot7.Utils.Constants.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import ie.baloot7.Utils.JWTUtility;
import ie.baloot7.exception.ErrorResponse;
import ie.baloot7.exception.UnauthorizedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(1)
public class AuthFilter extends OncePerRequestFilter {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var pathInfo = request.getServletPath();
        if(pathInfo.contains("login") || pathInfo.contains("register")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwtToken = request.getHeader(AUTH_TOKEN);

        try {
            if(jwtToken == null) {
                throw new UnauthorizedException("No JWT Token found");
            }
            if(!JWTUtility.validateToken(jwtToken)) {
                throw new UnauthorizedException("JWT Token Invalid");
            }
            if(JWTUtility.isTokenExpired(jwtToken)) {
                throw new UnauthorizedException("JWT Token expired");
            }
        }
        catch (UnauthorizedException e) {
            mapper.writeValueAsString(new ResponseEntity<>(new ErrorResponse(401, e.getMessage(), "Unauthorized"),
                    HttpStatus.UNAUTHORIZED));
        }
        filterChain.doFilter(request, response);
    }
}
