package Ecommerce.Reactive.MyData_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@EnableWebFlux
@SpringBootApplication
public class MyDataServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyDataServiceApplication.class, args);
	}

}
