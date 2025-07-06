package com.sky.minio;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@ConfigurationPropertiesScan("com.atguigu.lease.common.minio")
@EnableConfigurationProperties(MinioProperties.class)
public class MinioConfiguration {
    @Autowired
    private MinioProperties properties;

    @Bean
    public MinioClient myminioClient() {
        return MinioClient.builder().
                endpoint(properties.getEndpoint()).
                credentials(properties.getAccessKey(),
                            properties.getSecretKey())
                .build();
    }
}
