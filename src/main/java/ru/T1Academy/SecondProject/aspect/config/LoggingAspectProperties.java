package ru.T1Academy.SecondProject.aspect.config;

import org.slf4j.event.Level;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "logging.aspect")
public class LoggingAspectProperties {
    private boolean enabled;
    private Level level = Level.INFO;
}
