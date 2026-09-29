package kz.iitu.springlab.aspect;

import kz.iitu.springlab.audit.Audited;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Aspect
@Component
@Order(1)
public class AuditAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditAspect.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Around("@annotation(audited)")
    public Object audit(ProceedingJoinPoint pjp, Audited audited) throws Throwable {
        String action = audited.action();
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String method = pjp.getSignature().toShortString();

        if (audited.logArguments()) {
            log.info("[AUDIT] start {} at {} method={} args={}",
                    action, timestamp, method, Arrays.toString(pjp.getArgs()));
        } else {
            log.info("[AUDIT] start {} at {} method={}",
                    action, timestamp, method);
        }

        try {
            Object result = pjp.proceed();
            log.info("[AUDIT] {} success", action);
            return result;
        } catch (Throwable ex) {
            log.error("[AUDIT] {} failure: {} ({})",
                    action, ex.getClass().getSimpleName(), ex.getMessage());
            throw ex; // The exception must not be suppressed
        }
    }
}
