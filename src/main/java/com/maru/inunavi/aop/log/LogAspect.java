package com.maru.inunavi.aop.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
public class LogAspect {

    @Before("@annotation(com.maru.inunavi.aop.log.Log)")
    public void logBefore(JoinPoint joinPoint){
        String ipAddress = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest()
                .getRemoteAddr();

        log.info("["+ipAddress+"] calls {}", joinPoint.getSignature().getName());
    }
}