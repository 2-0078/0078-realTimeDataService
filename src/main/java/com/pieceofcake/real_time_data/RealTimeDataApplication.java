package com.pieceofcake.real_time_data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableDiscoveryClient
@SpringBootApplication
@EnableScheduling
public class RealTimeDataApplication {

	public static void main(String[] args) {
		SpringApplication.run(RealTimeDataApplication.class, args);
	}

}
