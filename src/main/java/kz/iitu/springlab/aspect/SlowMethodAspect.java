package kz.iitu.springlab.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Aspect
@Component
public class SlowMethodAspect {

    private static final Logger log = LoggerFactory.getLogger(SlowMethodAspect.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final long thresholdMs;
    private final List<SlowMethodRecord> slowMethods = new CopyOnWriteArrayList<>();

    public record SlowMethodRecord(
            String method,
            long durationMs,
            long thresholdMs,
            String timestamp,
            String arguments
    ) {}

    public SlowMethodAspect(@Value("${app.slow-threshold:200}") long thresholdMs) {
        this.thresholdMs = thresholdMs;
    }

    @Around("kz.iitu.springlab.aspect.Pointcuts.serviceOperation()")
    public Object trackSlowCalls(ProceedingJoinPoint pjp) throws Throwable {
        long started = System.currentTimeMillis();
        try {
            return pjp.proceed();
        } finally {
            long duration = System.currentTimeMillis() - started;
            if (duration >= thresholdMs) {
                String methodName = pjp.getSignature().toShortString();
                String timestamp = LocalDateTime.now().format(FORMATTER);
                String args = Arrays.toString(pjp.getArgs());
                SlowMethodRecord record = new SlowMethodRecord(
                        methodName, duration, thresholdMs, timestamp, args
                );
                slowMethods.add(record);
                log.info("[SLOW-LOG] Collected slow method call: {} (took {} ms >= threshold {} ms)",
                        methodName, duration, thresholdMs);
            }
        }
    }

    public List<SlowMethodRecord> getSlowMethods() {
        return Collections.unmodifiableList(slowMethods);
    }
}
