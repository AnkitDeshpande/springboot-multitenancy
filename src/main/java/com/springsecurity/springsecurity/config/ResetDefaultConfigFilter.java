package com.springsecurity.springsecurity.config;

import com.springsecurity.springsecurity.models.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ResetDefaultConfigFilter extends OncePerRequestFilter {

    /**
     * Filter for request to default configs.
     *
     * @param request     - The HttpServletRequest
     * @param response    - The HttpServletResponse
     * @param filterChain - The FilterChain
     * @throws ServletException - The ServletException
     * @throws IOException      - The IOException
     */
    @Override
    protected void doFilterInternal(final @NonNull HttpServletRequest request,
                                    final @NonNull HttpServletResponse response,
                                    final @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        /* Set user session to null */
        request.getSession().setAttribute("SESSION_USER", null);
        TenantContext.clear();
        filterChain.doFilter(request, response);
    }

}