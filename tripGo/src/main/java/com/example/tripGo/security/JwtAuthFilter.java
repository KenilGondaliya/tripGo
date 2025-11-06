    package com.example.tripGo.security;

    import com.example.tripGo.entity.User;
    import com.example.tripGo.repository.UserRepository;
    import jakarta.servlet.FilterChain;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.stereotype.Component;
    import org.springframework.web.filter.OncePerRequestFilter;
    import org.springframework.web.servlet.HandlerExceptionResolver;

    @Component
    @Slf4j
    @RequiredArgsConstructor
    public class JwtAuthFilter extends OncePerRequestFilter {

        private final UserRepository userRepo;
        private final AuthUtil authUtil;

        private final HandlerExceptionResolver handlerExceptionResolver;

        @Override
        protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
                throws java.io.IOException, jakarta.servlet.ServletException {
            try {
                String header = req.getHeader("Authorization");
                if (header == null || !header.startsWith("Bearer ")) {
                    chain.doFilter(req, res);
                    return;
                }

                String token = header.substring(7);
                String username = authUtil.getUsernameFromToken(token);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    User user = userRepo.findByUsername(username).orElseThrow();
                    var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
                chain.doFilter(req, res);
            } catch (Exception e) {
                handlerExceptionResolver.resolveException(req, res, null, e);
            }
        }
    }