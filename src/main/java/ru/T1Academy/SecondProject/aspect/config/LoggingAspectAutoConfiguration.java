package ru.T1Academy.SecondProject.aspect.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.T1Academy.SecondProject.aspect.aspect.LoggingAspect;

@Configuration
@EnableConfigurationProperties(LoggingAspectProperties.class)
@ConditionalOnProperty(prefix = "logging.aspect", name = "enabled", havingValue = "true", matchIfMissing = true)
public class LoggingAspectAutoConfiguration {

    LoggingAspectProperties loggingAspectProperties;

    public LoggingAspectAutoConfiguration(LoggingAspectProperties loggingAspectProperties)
    {
        this.loggingAspectProperties = loggingAspectProperties;
    }

    @Bean
    public LoggingAspect apiLoggingAspect(LoggingAspectProperties loggingAspectProperties) {
        return new LoggingAspect(loggingAspectProperties);
    }
}
