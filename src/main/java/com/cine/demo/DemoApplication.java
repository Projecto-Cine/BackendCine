package com.cine.demo;

import com.cine.demo.config.RequiredConfigValidator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(DemoApplication.class);
		// Comprueba que la configuracion obligatoria este completa antes de
		// instanciar beans, para que un secreto ausente de un error legible y no
		// un "Access denied for user" tres capas mas abajo.
		app.addInitializers(new RequiredConfigValidator());
		app.run(args);
	}

}