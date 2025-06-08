package com.mif.movieInsideForum.Property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@PropertySource("classpath:application.yml")
@ConfigurationProperties(prefix = "jwt")
@Component
public class JwtProperties {
    private String secretKey;
    private long expiration;
}
