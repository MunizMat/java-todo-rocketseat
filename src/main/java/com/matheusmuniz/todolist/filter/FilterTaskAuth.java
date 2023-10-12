package com.matheusmuniz.todolist.filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.matheusmuniz.todolist.user.IUserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        var authorization  = request.getHeader("Authorization");

        var authEncoded = authorization.substring("Basic".length()).trim();

        Base64.Decoder decoder = Base64.getDecoder();

        byte[] authDecode = decoder.decode(authEncoded);

        var authString = new String(authDecode);

        String[] credentials = authString.split(":");

        String username = credentials[0];
        String password = credentials[1];

        var user = this.userRepository.findByUsername(username);

        if(user == null) {
            response.sendError(401, "Usuário sem autorização");
        } else {
            var passwordVerified = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword().toCharArray());

            if(passwordVerified.verified){
                filterChain.doFilter(request, response);
            } else {
                response.sendError(401);
            }


        }

    }
}
