package com.iodsky.orderly.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@RequiredArgsConstructor
public class S3Config {

    private final Environment environment;

    @Bean
    public S3Client s3Client() {
        String activeProfile = environment.getActiveProfiles().length > 0
                ? environment.getActiveProfiles()[0]
                : "default";

        String region = environment.getProperty("aws.s3.region");

        AwsCredentialsProvider credentialsProvider;

        if (activeProfile.equalsIgnoreCase("local")) {
            String accessKey = environment.getProperty("aws.s3.access-key");
            String secretKey = environment.getProperty("aws.s3.secret-key");
            credentialsProvider = StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
        } else {
            credentialsProvider = DefaultCredentialsProvider.builder().build();
        }

        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider)
                .build();
    }

}
