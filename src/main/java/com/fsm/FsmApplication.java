package com.fsm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.io.iona.springboot.storage.StorageLocationProperties;

@SpringBootApplication(scanBasePackages={"com.fsm, com.io.iona.springboot.storage"})
@EnableEurekaClient
@EnableConfigurationProperties(StorageLocationProperties.class)
@EnableJpaAuditing
public class FsmApplication {

	public static void main(String[] args) {
		SpringApplication.run(FsmApplication.class, args);
	}
	
}