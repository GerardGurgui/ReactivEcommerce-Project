package Ecommerce.Reactive.MyData_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.reactive.config.EnableWebFlux;

@EnableWebFlux
@EnableDiscoveryClient
@SpringBootApplication
public class MyDataServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyDataServiceApplication.class, args);
	}

}
