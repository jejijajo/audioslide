package com.audioslide.upload.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Setter
@Configuration
@Slf4j
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {

    private String endpoint;
    private String accessKey;
    private String secretKey;
    private List<String> buckets;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey,secretKey)
                .build();
    }

    @Bean
    public CommandLineRunner initializeMinio(MinioClient minioClient) {
        return args -> {
            for (String bucket : buckets) {
                try {
                    boolean found = minioClient.bucketExists(
                            BucketExistsArgs.builder().bucket(bucket).build()
                    );

                    if (!found) {
                        minioClient.makeBucket(
                                MakeBucketArgs.builder().bucket(bucket).build()
                        );
                        log.info("Bucket '{}' created", bucket);
                    } else {
                        log.info("Bucket '{}' already exists", bucket);
                    }
                } catch (Exception e) {
                    throw new IllegalStateException("Failed to check/create bucket '"+bucket+"'", e);
                }
            }
        };
    }
}
