package com.validators.userms.aop;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {
    private final Logger logger = LogManager.getLogger(this.getClass());

    @Pointcut("execution(* com.validators.userms.service.impl.UserServiceImpl.registerUser(..))")
    public void userServiceMethods() {
    }

    @Before("userServiceMethods()")
    public void beforeUserServiceEntry(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        logger.info("Method invoked: {}", methodName);
        logger.info("Arguments: {}", Arrays.toString(args));
    }

    @After("userServiceMethods()")
    public void afterUserServiceExit(JoinPoint joinPoint) {
        logger.info("Exiting method: {}", joinPoint.getSignature().getName());
    }

    @AfterThrowing(pointcut = "userServiceMethods()", throwing = "exception")
    public void handleUserServiceException(Exception exception) {
        logger.error("Exception occurred in userServiceImpl: {}", exception.getMessage(), exception);
    }
}
