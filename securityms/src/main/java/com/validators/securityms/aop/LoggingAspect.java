package com.validators.securityms.aop;

import com.validators.securityms.exception.CustomException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {
    private final Logger logger = LogManager.getLogger(this.getClass());

    /**
     *
     * User Service impl
     */

    // Pointcut for all methods in UserServiceImpl
    @Pointcut("execution(* com.validators.securityms.service.impl.UserServiceImpl.*(..))")
    public void userServiceMethods() {
    }

    // Log method entry
    @Before("userServiceMethods()")
    public void userServiceMethodEntry(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        logger.info("Method invoked: {}", methodName);
        logger.info("Arguments: {}", Arrays.toString(args));
    }

    // Handle exceptions thrown by UserServiceImpl methods
    @AfterThrowing(pointcut = "userServiceMethods()", throwing = "exception")
    public void handleUserServiceException(Exception exception) throws CustomException {
        logger.error("Exception occurred in UserServiceImpl: {}", exception.getMessage(), exception);
        throw new CustomException(exception.getMessage(), exception);
    }


    /**
     *
     * Jwt Service Impl
     */
    // Pointcut for all methods in JwtServiceImpl
    @Pointcut("execution(* com.validators.securityms.service.impl.JwtServiceImpl.*(..))")
    public void jwtServiceMethods() {
    }

    // Log method entry
    @Before("jwtServiceMethods()")
    public void jwtServiceMethodEntry(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        logger.info("Method invoked: {}", methodName);
        logger.info("Arguments: {}", Arrays.toString(args));
    }

    // Handle exceptions thrown by JwtServiceImpl methods
    @AfterThrowing(pointcut = "jwtServiceMethods()", throwing = "exception")
    public void handleJwtServiceException(Exception exception) throws CustomException {
        logger.error("Exception occurred in JwtServiceImpl: {}", exception.getMessage(), exception);
        throw new CustomException(exception.getMessage(),exception);
    }

}
