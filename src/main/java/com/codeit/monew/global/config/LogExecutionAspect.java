package com.codeit.monew.global.config;

import com.codeit.monew.global.annotation.LogExecution;
import com.codeit.monew.global.annotation.LogTag;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j
public class LogExecutionAspect {

    @Around("@annotation(com.codeit.monew.global.annotation.LogExecution)" +
            " || @within(com.codeit.monew.global.annotation.LogExecution)")
    public Object logExecution22(ProceedingJoinPoint joinPoint) throws Throwable {

        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Class<?> targetClass = joinPoint.getTarget().getClass();

        // 메서드 우선 → 없으면 클래스
        LogExecution ann = resolveAnnotation(method, targetClass);
        if (ann == null) {
            return joinPoint.proceed();
        }

        String tag = resolveTag(ann);
        String methodName = joinPoint.getSignature().toShortString();
        String tagPrefix = (tag == null || tag.isBlank()) ? "" : "[" + tag + "] ";
        Object[] args = joinPoint.getArgs();
        if (!ann.time()) {
            try {
                log.info("[START] {}{}", tagPrefix, methodName);
                Object result = joinPoint.proceed();
                log.info("[SUCCESS] {}{}", tagPrefix, methodName);
                return result;
            } catch (Exception e) {
                String argLog = buildArgLog(joinPoint);
                log.warn(
                        "[ERROR] {}{} args=[{}]",
                        tagPrefix,
                        methodName,
                        argLog,
                        e
                );
                throw e;
            }
        }

        long start = System.currentTimeMillis();
        try {
            log.info("[START] {}{}", tagPrefix, methodName);
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - start;
            log.info("[SUCCESS] {}{} ({} ms)", tagPrefix, methodName, duration);
            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - start;
            String argLog = buildArgLog(joinPoint);
            log.warn(
                    "[ERROR] {}{}({} ms) args=[{}]",
                    tagPrefix,
                    methodName,
                    duration,
                    argLog,
                    e
            );
            throw e;
        }
    }

    //변수명 : 변수값 매칭
    private String buildArgLog(ProceedingJoinPoint joinPoint) {
        MethodSignature sig = (MethodSignature) joinPoint.getSignature();
        String[] names = sig.getParameterNames();
        Object[] args = joinPoint.getArgs();

        if (names == null || args == null) return "";

        StringBuilder sb = new StringBuilder();
        int len = Math.min(names.length, args.length);

        for (int i = 0; i < len; i++) {
            sb.append(names[i])
                    .append("=")
                    .append(args[i]);
            if (i < len - 1) sb.append(", ");
        }
        return sb.toString();
    }

    //태그 or 커스텀 판별기
    private String resolveTag(LogExecution ann) {

        if (ann.tag() != LogTag.NONE) {
            return ann.tag().name();
        }
        if (!ann.value().isBlank()) {
            return ann.value();
        }
        return "";
    }
    //클래스,메서드 겹치면  메서드우선적용
    private LogExecution resolveAnnotation(Method method, Class<?> targetClass) {
        LogExecution methodAnn = AnnotatedElementUtils.findMergedAnnotation(method, LogExecution.class);
        if (methodAnn != null) return methodAnn;
        return AnnotatedElementUtils.findMergedAnnotation(targetClass, LogExecution.class);
    }

}