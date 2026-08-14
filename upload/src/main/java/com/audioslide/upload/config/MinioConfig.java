package com.audioslide.upload.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Setter
@Configuration
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
                    // Проверяем, существует ли бакет
                    boolean found = minioClient.bucketExists(
                            BucketExistsArgs.builder().bucket(bucket).build()
                    );

                    if (!found) {
                        // Если бакета нет — создаем его
                        minioClient.makeBucket(
                                MakeBucketArgs.builder().bucket(bucket).build()
                        );
                        System.out.printf("Bucket '%s' successfully created.%n", bucket);
                    } else {
                        System.out.printf("Bucket '%s' already exists.%n", bucket);
                    }
                } catch (Exception e) {
                    System.err.printf("Failed to check/create bucket '%s': %s%n", bucket, e.getMessage());
                }
            }
        };
    }
}
