package Ecommerce.Reactive.UserAuthentication_service;

import Ecommerce.Reactive.UserAuthentication_service.config.SecurityConfigDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@SpringBootApplication
@EnableConfigurationProperties(SecurityConfigDataSource.class)
@EnableDiscoveryClient
public class UserAuthenticationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserAuthenticationServiceApplication.class, args);
	}

}
