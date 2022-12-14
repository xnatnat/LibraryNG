package br.com.newgo.api.libraryng;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(title = "LibraryNG API", version = "1.0.0"),
		tags = {@Tag(name= "Books", description = "Book CRUD requests and responses."),
				@Tag(name= "Authors", description = "Author CRUD requests and responses.")}
)

public class LibraryngApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryngApplication.class, args);
	}

}
