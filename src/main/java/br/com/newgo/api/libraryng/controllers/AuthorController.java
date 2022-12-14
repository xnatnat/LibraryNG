package br.com.newgo.api.libraryng.controllers;

import br.com.newgo.api.libraryng.dtos.AuthorDto;
import br.com.newgo.api.libraryng.models.Author;
import br.com.newgo.api.libraryng.services.IAuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/v1/authors")
public class AuthorController {

    final IAuthorService authorService;

    public AuthorController(IAuthorService authorService){this.authorService = authorService;}

    @PostMapping
    @Operation(tags = {"Authors"},
            operationId = "SaveAuthor",
            description = "This method saves an author")
    @ApiResponse(responseCode = "201", description = "Request Success - The request has been fulfilled and has resulted in one or more new resources being created.", content = {
            @Content(mediaType = "APPLICATION/JSON", schema = @Schema(implementation = Author.class))})
    public ResponseEntity<Object> saveAuthor(@RequestBody @Valid AuthorDto authorDto){
        var author = new Author();
        BeanUtils.copyProperties(authorDto, author);
        author.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC")));
        return ResponseEntity.status(HttpStatus.CREATED).body(authorService.save(author));
    }

    @GetMapping
    @Operation(tags = {"Authors"},
            operationId = "GetAllAuthors",
            description = "This method returns all registered authors")
    @ApiResponse(responseCode = "200", description = "Request Success", content = {
                    @Content(mediaType = "APPLICATION/JSON", schema = @Schema(implementation = Author.class))})
    public ResponseEntity<List<Author>> getAllAuthors(){
        return ResponseEntity.status(HttpStatus.OK).body(authorService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(tags = {"Authors"},
            operationId = "GetAuthorById",
                description = "This method returns an author by ID")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Request Success", content = {
                        @Content(mediaType = "APPLICATION/JSON", schema = @Schema(implementation = Author.class))}),
                @ApiResponse(responseCode = "404", description = "Response Not Found", content =
                @Content(mediaType = "MESSAGE/STRING", schema = @Schema(example = "Author not found.")))})
    public ResponseEntity<Object> getAuthorById(@PathVariable(value = "id") UUID id){
        Optional<Author> author = authorService.findById(id);
        //TODO criar custom validator
        if(!author.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Author not found.");
        return ResponseEntity.status(HttpStatus.OK).body(author.get());
    }

    @Transactional
    @DeleteMapping("/{id}")
    @Operation(tags = {"Authors"},
            operationId = "DeleteAuthorById",
            description = "This method deletes an author by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request Success", content = {
                    @Content(mediaType = "MESSAGE/STRING", schema = @Schema(example = "Author deleted successfully."))}),
            @ApiResponse(responseCode = "409", description = "Conflict", content = {
                    @Content(mediaType = "MESSAGE/STRING", schema = @Schema(example = "Author contains registered books."))}),
            @ApiResponse(responseCode = "404", description = "Response Not Found", content =
                    @Content(mediaType = "MESSAGE/STRING", schema = @Schema(example = "Author not found.")))})
    public ResponseEntity<Object> deleteById(@PathVariable(value = "id") UUID id){
        Optional<Author> author = authorService.findById(id);
        if(!author.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Author not found.");
        if(author.get().getBooks().isEmpty())
            authorService.delete(author.get());
        else
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Author contains registered books.");
        return ResponseEntity.status(HttpStatus.OK).body("Author deleted successfully.");
    }

    @PutMapping("/{id}")
    @Operation(tags = {"Authors"},
            operationId = "PutAuthorById",
            description = "This method updates an author by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Request Success - The request has been fulfilled and has resulted in one or more new resources being created.", content = {
                    @Content(mediaType = "APPLICATION/JSON", schema = @Schema(implementation = Author.class))}),
            @ApiResponse(responseCode = "404", description = "Response Not Found", content =
                    @Content(mediaType = "MESSAGE/STRING", schema = @Schema(example = "Author not found.")))})
    public ResponseEntity<Object> updateAuthor(@PathVariable(value = "id") UUID id,
                                               @RequestBody @Valid AuthorDto authorDto){
        Optional<Author> authorOptional = authorService.findById(id);
        if(!authorOptional.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Author not found.");
        Author author = new Author();
        BeanUtils.copyProperties(authorDto, author);
        author.setId(authorOptional.get().getId());
        author.setRegistrationDate(authorOptional.get().getRegistrationDate());
        return ResponseEntity.status(HttpStatus.CREATED).body(authorService.save(author));
    }
}