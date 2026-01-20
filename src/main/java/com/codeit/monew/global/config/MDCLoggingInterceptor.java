package com.codeit.monew.global.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class MDCLoggingInterceptor extends OncePerRequestFilter {

    public static final String MDC_REQUEST_ID = "requestId";
    public static final String MDC_CLIENT_IP = "clientIp";


    public static final String HEADER_REQUEST_ID = "Header-Request-Id";
    public static final String HEADER_CLIENT_IP = "Header-Client-Ip";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestId = resolveRequestId(request);
        String clientIp = resolveClientIp(request);

        MDC.put(MDC_REQUEST_ID, requestId);
        MDC.put(MDC_CLIENT_IP, clientIp);

        response.setHeader(HEADER_REQUEST_ID, requestId);
        response.setHeader(HEADER_CLIENT_IP, clientIp);

        try {
            filterChain.doFilter(request, response);
        } finally {

            MDC.clear();
        }
    }

    private String resolveRequestId(HttpServletRequest request) {
        // 클라이언트가 이미 X-Request-Id 를 보내오면 그걸 유지 (트레이싱 연동에 좋음)
        String incoming = request.getHeader(HEADER_REQUEST_ID);
        if (incoming != null && !incoming.isBlank()) return incoming.trim();
        return UUID.randomUUID().toString();
    }

    //오케 다물어봐 수퍼똑똑이다
    private String resolveClientIp(HttpServletRequest request) {
        // 프록시/ALB 환경 고려: X-Forwarded-For 첫 번째 값이 보통 원 IP
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        // 표준 대안
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }
        return request.getRemoteAddr();
    }
}
