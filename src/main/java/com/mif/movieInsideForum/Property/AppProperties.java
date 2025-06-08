package com.mif.movieInsideForum.Property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@PropertySource("classpath:application.yml")
@ConfigurationProperties(prefix = "app")
@Component
public class AppProperties {
    private String host;
}
