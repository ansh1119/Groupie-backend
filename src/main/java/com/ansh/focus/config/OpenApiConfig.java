package com.ansh.focus.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Value("${server.port:8080}")
	private int serverPort;

	@Bean
	public OpenAPI focusOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("Focus API")
						.description("""
								REST API for the Focus application.

								## Authentication
								Most endpoints require a JWT Bearer token. Obtain a token via:
								- `POST /auth/signup` — register a new account
								- `POST /auth/login` — sign in with username or email

								Include the token on protected requests:
								```
								Authorization: Bearer <token>
								```

								## Database
								Persisted with PostgreSQL (Supabase) via Spring Data JPA.

								## Groups
								Groups use a `GroupMember` junction entity (not @ManyToMany).
								The creator is automatically added as `OWNER` on group creation.
								""")
						.version("1.0.0")
						.contact(new Contact()
								.name("Focus")
								.email("support@focus.app"))
						.license(new License()
								.name("Apache 2.0")
								.url("https://www.apache.org/licenses/LICENSE-2.0")))
				.addServersItem(new Server()
						.url("http://localhost:" + serverPort)
						.description("Local development server"))
				.components(new Components()
						.addSecuritySchemes("bearerAuth", new SecurityScheme()
								.name("bearerAuth")
								.type(SecurityScheme.Type.HTTP)
								.scheme("bearer")
								.bearerFormat("JWT")
								.description("JWT access token returned by /auth/signup or /auth/login")));
	}
}
