package com.mif.movieInsideForum.Property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "event")
@Getter
@Setter
public class EventProperties {
    private String frontendHost;
    private int notificationMinutesBefore;
} 