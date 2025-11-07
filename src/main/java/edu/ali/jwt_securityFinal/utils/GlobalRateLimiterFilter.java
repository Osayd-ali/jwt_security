package edu.ali.jwt_securityFinal.utils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// This filter implements a global rate limiter based on client IP addresses.
// It restricts the number of requests a client can make within a specified time window.
@Component
public class GlobalRateLimiterFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS = 100; // per time window
    private static final long WINDOW_MS = 60_000; // 1 minute

    private final Map<String, RequestWindow> ipRequests = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String ip = request.getRemoteAddr();
        Instant now = Instant.now();

        RequestWindow window = ipRequests.computeIfAbsent(ip, k -> new RequestWindow());

        synchronized (window) { // Synchronize on the window object to ensure thread safety, for multiple requests coming from same IP
            if (now.isAfter(window.windowStart.plusMillis(WINDOW_MS))) {
                window.windowStart = now;
                window.requestCount = 1;
            } else {
                window.requestCount++;
            }

            if (window.requestCount > MAX_REQUESTS) {
                response.setStatus(429);
                response.setContentType("application/json");
                String s = String.format("""
                    {
                      "error": "Rate Limit Exceeded",
                      "message": "Exceeded %d calls per %d seconds. Please slow down."
                    }
                """, MAX_REQUESTS, WINDOW_MS/1000);
                response.getWriter().write(s);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private static class RequestWindow {
        Instant windowStart = Instant.now();
        int requestCount = 0;
    }
}

