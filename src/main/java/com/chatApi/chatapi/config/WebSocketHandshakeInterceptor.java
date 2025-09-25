package com.chatApi.chatapi.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.security.Principal;
import java.util.Map;

/**
 * Handshake interceptor that extracts JWT from:
 *  - "Authorization" header (Bearer <token>)
 *  - OR "access_token" query parameter
 *
 * If valid, it puts a Principal with username into the attributes map under key "principal".
 * Spring will map handshake attributes to session attributes and the Principal will be available
 * to messaging.
 */
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(WebSocketHandshakeInterceptor.class);

    private final JwtTokenProvider jwtTokenProvider;

    public WebSocketHandshakeInterceptor() {
        // can't autowire here because this interceptor is constructed directly in WebSocketConfig.
        // We'll obtain JwtTokenProvider statically using the AppContextHolder trick.
        this.jwtTokenProvider = BeanUtil.getBean(JwtTokenProvider.class);
    }

    @Override
    public boolean beforeHandshake(org.springframework.http.server.ServerHttpRequest request,
                                   org.springframework.http.server.ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {

        if (request instanceof ServletServerHttpRequest servletReq) {
            HttpServletRequest httpServletRequest = servletReq.getServletRequest();

            // 1) Look for Authorization header
            String token = null;
            String authHeader = httpServletRequest.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }

            // 2) fallback to access_token query param
            if (token == null || token.isBlank()) {
                String q = httpServletRequest.getParameter("access_token");
                if (q != null && !q.isBlank()) token = q;
            }

            if (token != null && jwtTokenProvider.validateToken(token)) {
                String username = jwtTokenProvider.getUsernameFromJWT(token);
                if (username != null) {
                    // create a simple Principal that returns username
                    Principal principal = () -> username;
                    // store so Spring Messaging can pick it up
                    attributes.put("principal", principal);
                    // also set as standard Principal name under attribute "user"
                    attributes.put("user", principal);
                    LOG.debug("WebSocket handshake: authenticated user={}", username);
                }
            } else {
                LOG.debug("WebSocket handshake: no valid JWT provided (anonymous session)");
            }
        }

        return true; // allow handshake (we allow anonymous too)
    }

    @Override
    public void afterHandshake(org.springframework.http.server.ServerHttpRequest request,
                               org.springframework.http.server.ServerHttpResponse response,
                               WebSocketHandler wsHandler, @Nullable Exception exception) {
        // noop
    }
}
