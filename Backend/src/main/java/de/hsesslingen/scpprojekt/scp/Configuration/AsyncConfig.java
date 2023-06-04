package de.hsesslingen.scpprojekt.scp.Configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Enable async processing and allow configuration if desired
 * @author Jason Patrick Duffy
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}
