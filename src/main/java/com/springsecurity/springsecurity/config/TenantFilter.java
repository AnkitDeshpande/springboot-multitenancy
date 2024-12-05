package com.springsecurity.springsecurity.config;

import com.springsecurity.springsecurity.entity.User;
import com.springsecurity.springsecurity.models.TenantContext;
import com.springsecurity.springsecurity.service.impl.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
public class TenantFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantFilter.class);

    @Lazy
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(final @NonNull HttpServletRequest request,
                                    final @NonNull HttpServletResponse response,
                                    final @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {

            User commonUser = (User) request.getSession()
                    .getAttribute("SESSION_USER");

            if (Objects.isNull(commonUser)) {
                filterChain.doFilter(request, response);
                return;
            }

            LOGGER.info("Common user {}", commonUser.getUsername());
            String schemaName = commonUser.getSchemaName();
            LOGGER.info("Tenant schema {}", schemaName);

            if (!StringUtils.hasText(schemaName)) {
                throw new AccessDeniedException("Access Denied.");
            }

            TenantContext.setCurrentTenant(schemaName);
            LOGGER.info(" #1 Resolve current tenant {}", TenantContext.getCurrentTenant());

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            User user = (User) userDetailsService.loadUserByUsername(commonUser.getUsername());

            if (Objects.isNull(user)) {
                throw new AccessDeniedException("Access Denied.");
            }

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(user, null,
                            user.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            context.setAuthentication(authToken);
            SecurityContextHolder.setContext(context);

            LOGGER.info("request.getContextPath() -> {}", request.getContextPath());
            LOGGER.info("request.getRequestURI() -> {}", request.getRequestURI());
            String requestUri = request.getRequestURI();
            if (StringUtils.hasText(request.getContextPath())) {
                requestUri = request.getRequestURI().replace(request.getContextPath(), "");
            }

            LOGGER.info(" #3 Resolve current tenant {}", TenantContext.getCurrentTenant());

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            request.getSession().setAttribute("SESSION_USER", null);
            TenantContext.clear();
            LOGGER.info(" #4 Resolve current tenant {}", TenantContext.getCurrentTenant());
        }
    }
}