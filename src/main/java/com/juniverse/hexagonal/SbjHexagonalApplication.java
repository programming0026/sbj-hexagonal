package com.juniverse.hexagonal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** CRUD de usuarios con arquitectura hexagonal (puertos y adaptadores), Spring Boot y MySQL. */
@SpringBootApplication
public class SbjHexagonalApplication {

	/**
	 * Arranca la aplicación.
	 * @param args argumentos de la línea de comandos
	 */
	public static void main(String[] args) {
		SpringApplication.run(SbjHexagonalApplication.class, args);
	}

}
