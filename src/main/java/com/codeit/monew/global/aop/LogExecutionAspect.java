package com.codeit.monew.global.aop;

import com.codeit.monew.global.aop.annotation.LogExecution;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;

import static com.codeit.monew.global.aop.LogExecutionUtils.resolveAnnotation;
import static com.codeit.monew.global.aop.LogExecutionUtils.*;


@Aspect
@Component
@Slf4j
public class LogExecutionAspect {

    @Around("@annotation(com.codeit.monew.global.aop.annotation.LogExecution)" +
            " || @within(com.codeit.monew.global.aop.annotation.LogExecution)")
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
        if (!ann.time()) {
            try {
                log.info("[START] {}{}", tagPrefix, methodName);
                Object result = joinPoint.proceed();
                log.info("[SUCCESS] {}{}", tagPrefix, methodName);
                return result;
            } catch (Exception e) {
                log.warn(
                        "[FAIL] {}{}",
                        tagPrefix,
                        methodName
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
            log.warn(
                    "[FAIL] {}{}  ({} ms)",
                    tagPrefix,
                    methodName,
                    duration
            );
            throw e;
        }
    }


}