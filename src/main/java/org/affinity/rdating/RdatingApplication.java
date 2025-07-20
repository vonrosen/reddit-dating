package org.affinity.rdating;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
public class RdatingApplication {

	public static void main(String[] args) {
		SpringApplication.run(RdatingApplication.class, args);
	}

}
