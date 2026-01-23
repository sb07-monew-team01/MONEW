package com.codeit.monew.global.config;

import com.codeit.monew.global.annotation.LogExecution;
import com.codeit.monew.global.annotation.LogTag;
import com.codeit.monew.global.exception.MonewException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.stream.Collectors;

public final class LogExecutionUtils {

    private LogExecutionUtils() {}

    // 변수명 : 변수값 매칭
    public static String buildArgLog(ProceedingJoinPoint joinPoint) {
        MethodSignature sig = (MethodSignature) joinPoint.getSignature();
        String[] names = sig.getParameterNames();
        Object[] args = joinPoint.getArgs();

        if (names == null || args == null) return "";

        int len = Math.min(names.length, args.length);
        if (len == 0) return "";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(names[i]).append("=").append(args[i]);
            if (i < len - 1) sb.append(", ");
        }
        return sb.toString();
    }

    // 태그 or 커스텀 판별기
    public static String resolveTag(LogExecution ann) {
        if (ann == null) return "";

        if (ann.tag() != LogTag.NONE) {
            return ann.tag().name();
        }
        if (!ann.value().isBlank()) {
            return ann.value();
        }
        return "";
    }

    // details
    public static String resolveDetail(Throwable e) {
        if (e instanceof MonewException me) {
            Map<String, Object> details = me.getDetails();
            if (details == null || details.isEmpty()) return "";

            String keyValue = details.entrySet().stream()
                    .map(en -> en.getKey() + "= " + en.getValue())
                    .collect(Collectors.joining(", "));

            return " [details : [" + keyValue + "]]";
        }
        return "";
    }



    // 클래스, 메서드 겹치면 메서드 우선 적용
    public static LogExecution resolveAnnotation(Method method, Class<?> targetClass) {
        LogExecution methodAnn = AnnotatedElementUtils.findMergedAnnotation(method, LogExecution.class);
        if (methodAnn != null) return methodAnn;
        return AnnotatedElementUtils.findMergedAnnotation(targetClass, LogExecution.class);
    }
}
