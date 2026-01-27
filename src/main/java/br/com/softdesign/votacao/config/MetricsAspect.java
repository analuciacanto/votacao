package br.com.softdesign.votacao.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class MetricsAspect {

    private final MeterRegistry meterRegistry;

    /**
     Intercepta todos os métodos nos serviços (br.com.softdesign.votacao.service..*).
     Se lançar uma exceção, incrementa um Counter automaticamente. **/

    @Around("execution(* br.com.softdesign.votacao.service..*(..))")
    public Object countErrors(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            // Cria/Incrementa um contador por tipo de exceção
            String metricName = "service.erros." + e.getClass().getSimpleName();
            Counter counter = meterRegistry.counter(metricName);
            counter.increment();
            throw e;
        }
    }
}
