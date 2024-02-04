package com.b2012202.mxhtknt.Configuration;


import com.b2012202.mxhtknt.Services.JWTService;
import com.b2012202.mxhtknt.Services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    private final JWTService jwtService;

    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request , HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            final String authHeader = request.getHeader("Authorization");
            final String jwt;
            final String userName;

            if(StringUtils.isEmpty(authHeader) || !authHeader.startsWith("Bearer")){
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                response.setContentType("application/json");
//                response.setCharacterEncoding("UTF-8");
//
//                try (PrintWriter writer = response.getWriter()) {
//                    writer.write("{\"error\": \"1111Unauthorized - Invalid Token\"}");
//                }
                filterChain.doFilter(request, response);
                return;
            }

            jwt= authHeader.substring(7);
            System.out.println("~~~>JWT "+ jwt);
            userName= jwtService.extractUsername(jwt);
            long id= jwtService.extractId(jwt);
            System.out.println("~~~>FILTER ");
            System.out.println("~~~~~~>userName "+ userName);
            System.out.println("~~~~~~>id "+ id);

            if(StringUtils.isNotEmpty(userName) && SecurityContextHolder.getContext().getAuthentication() == null){
                UserDetails userDetails= userService.userDetailsService().loadUserByUsername(userName);
                System.out.println("~~~Present User~~~> " + userDetails.toString());
                if(jwtService.isTokenValid(jwt,userDetails)){
                    SecurityContext securityContext= SecurityContextHolder.createEmptyContext();

                    UsernamePasswordAuthenticationToken token= new UsernamePasswordAuthenticationToken(
                            userDetails,null,userDetails.getAuthorities()
                    );

                    token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    securityContext.setAuthentication(token);

                    SecurityContextHolder.setContext(securityContext);
                    System.out.println("Context "+ SecurityContextHolder.getContext().getAuthentication().getAuthorities());
                }
//                else{
//                    // Ném ra một thông báo lỗi nếu token không hợp lệ
//                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                    response.setContentType("application/json");
//                    response.setCharacterEncoding("UTF-8");
//
//                    try (PrintWriter writer = response.getWriter()) {
//                        writer.write("{\"error\": \"2222Unauthorized - Invalid Token\"}");
//                    }
//                    return;
//                }
            }
            filterChain.doFilter(request, response);
        }catch (Exception ex){
            // Ném ra một thông báo lỗi chung nếu có lỗi xảy ra
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            try (PrintWriter writer = response.getWriter()) {
                writer.write("{\"error\": \"Internal Server Error\"}");
            }
            throw new RuntimeException(ex.getCause());
        }
    }
}
