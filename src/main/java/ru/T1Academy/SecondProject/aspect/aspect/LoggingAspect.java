package ru.T1Academy.SecondProject.aspect.aspect;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.T1Academy.SecondProject.aspect.config.LoggingAspectProperties;
import ru.T1Academy.SecondProject.aspect.exception.LoggingAspectException;


import java.lang.reflect.Method;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    private final LoggingAspectProperties loggingAspectProperties;

    public LoggingAspect(LoggingAspectProperties loggingAspectProperties) {
        this.loggingAspectProperties = loggingAspectProperties;
    }

    private void loggingAspect(String format, Object... arguments) {
        switch (loggingAspectProperties.getLevel()) {
            case ERROR -> log.error(format, arguments);
            case WARN -> log.warn(format, arguments);
            case INFO -> log.info(format, arguments);
            case DEBUG -> log.debug(format, arguments);
            case TRACE -> log.trace(format, arguments);
        }
    }

    public boolean isApiEndpointMethod(JoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        if (method.getDeclaringClass().isAnnotationPresent(RestController.class) ||
                method.getDeclaringClass().isAnnotationPresent(Controller.class)) {

            return method.isAnnotationPresent(RequestMapping.class) ||
                    method.isAnnotationPresent(GetMapping.class) ||
                    method.isAnnotationPresent(PostMapping.class) ||
                    method.isAnnotationPresent(PutMapping.class) ||
                    method.isAnnotationPresent(DeleteMapping.class) ||
                    method.isAnnotationPresent(PatchMapping.class);
        }

        return false;
    }



    @Before(value = "@within(ru.T1Academy.SecondProject.aspect.annotation.LoggingBefore)")
    public void loggingBefore(JoinPoint joinPoint) {
        if(isApiEndpointMethod(joinPoint))
        {
            loggingAspect("Метод: {}->{}, запустился с аргументами: {}.",
                    ((MethodSignature) joinPoint.getSignature()).getMethod().getDeclaringClass().getSimpleName(),
                    joinPoint.getSignature().getName(),
                    Arrays.toString(joinPoint.getArgs()));
        }
    }

    @AfterThrowing(
            value = "@annotation(ru.T1Academy.SecondProject.aspect.annotation.LoggingAfterThrowing)",
            throwing = "exception")
    public void loggingAfterThrowing(JoinPoint joinPoint, Exception exception) {
        if(isApiEndpointMethod(joinPoint))
        {
            loggingAspect("Метод: {}->{}, бросил ошибку: {}.",
                    ((MethodSignature) joinPoint.getSignature()).getMethod().getDeclaringClass().getSimpleName(),
                    joinPoint.getSignature().getName(),
                    exception.getMessage());
        }
    }

    @AfterReturning(
            value = "@annotation(ru.T1Academy.SecondProject.aspect.annotation.LoggingAfterReturning)",
            returning = "result")
    public void loggingAfterReturning(JoinPoint joinPoint, Object result) {
        if(isApiEndpointMethod(joinPoint))
        {
            loggingAspect("Метод: {}->{}, вернул: {}.",
                    ((MethodSignature) joinPoint.getSignature()).getMethod().getDeclaringClass().getSimpleName(),
                    joinPoint.getSignature().getName(),
                    result != null ? result.toString() : "null");
        }
    }

    @Around(value = "@annotation(ru.T1Academy.SecondProject.aspect.annotation.LoggingAround)")
    public Object loggingAround(ProceedingJoinPoint joinPoint) {
        Object result;

        long startTime = System.currentTimeMillis();
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            throw new LoggingAspectException("LoggingAspect получил ошибку во время выполнения метода: " + joinPoint.getSignature().getName());
        }
        long endTime = System.currentTimeMillis();

        if(isApiEndpointMethod(joinPoint)) {
            loggingAspect("Метод: {}->{}, выполнился за {} _ms.",
                    ((MethodSignature) joinPoint.getSignature()).getMethod().getDeclaringClass().getSimpleName(),
                    joinPoint.getSignature().getName(),
                    (endTime - startTime));
        }

        return result;
    }


}