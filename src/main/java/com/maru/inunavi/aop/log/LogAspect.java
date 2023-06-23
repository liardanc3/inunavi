package com.maru.inunavi.aop.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
public class LogAspect {

    @Before("@annotation(logger)")
    public void logBefore(JoinPoint joinPoint, Log logger){
        if(logger.value().equals("")){
            String ip = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                    .getRequest()
                    .getRemoteAddr();

            log.info("[" + ip + "] calls {}", joinPoint.getSignature().getName());
        }
        else{
            log.info(logger.value(), joinPoint.getArgs()[0]);
        }
    }
}