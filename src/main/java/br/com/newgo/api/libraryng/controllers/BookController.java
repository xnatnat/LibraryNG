package br.com.newgo.api.libraryng.controllers;

import br.com.newgo.api.libraryng.dtos.BookDto;
import br.com.newgo.api.libraryng.models.Book;
import br.com.newgo.api.libraryng.models.Author;
import br.com.newgo.api.libraryng.services.IAuthorService;
import br.com.newgo.api.libraryng.services.IBookService;
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
import java.util.*;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/v1/books")

public class BookController {

    final IBookService bookService;

    final IAuthorService authorService;
    public BookController(IBookService bookService, IAuthorService authorService){

        this.bookService = bookService;
        this.authorService = authorService;
    }

    @PostMapping
    @Operation(tags = {"Books"},
                operationId = "SaveBook",
                description = "This method saves a book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Request Success - The request has been fulfilled and has resulted in one or more new resources being created.", content = {
                    @Content(mediaType = "APPLICATION/JSON", schema = @Schema(implementation = Book.class))}),
            @ApiResponse(responseCode = "409", description = "Book Conflict", content = {
                    @Content(mediaType = "MESSAGE/STRING", schema = @Schema(example = "Conflict: Registered Book."))}),
            @ApiResponse(responseCode = "409", description = "Author Conflict", content = {
                    @Content(mediaType = "MESSAGE/STRING", schema = @Schema(example = "Conflict: Unregistered Author."))})})
    public ResponseEntity<Object> saveBook(@RequestBody @Valid BookDto bookDto){
        if(bookService.existsByIsbn(bookDto.getIsbn()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Registered Book.");
        var book = new Book();
        BeanUtils.copyProperties(bookDto, book, "authors");
        book.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC")));

        //TODO refatorar validação (tirar injeçao author e criar customValidator?)
        return getObjectResponseEntity(bookDto, book);
    }
    @GetMapping
    @Operation(tags = {"Books"},
            operationId = "GetAllBooks",
            description = "This method returns all registered books")
    @ApiResponse(responseCode = "200", description = "Request Success", content = {
            @Content(mediaType = "APPLICATION/JSON", schema = @Schema(implementation = Book.class))})
    public ResponseEntity<List<Book>> getAllBooks(){
        return ResponseEntity.status(HttpStatus.OK).body(bookService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(tags = {"Books"},
            operationId = "GetBookById",
            description = "This method returns a book by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request Success", content = {
                    @Content(mediaType = "APPLICATION/JSON", schema = @Schema(implementation = Book.class))}),
            @ApiResponse(responseCode = "404", description = "Response Not Found", content =
            @Content(mediaType = "MESSAGE/STRING", schema = @Schema(example = "Book not found.")))})
    public ResponseEntity<Object> getBookById(@PathVariable(value="id") UUID id){
        Optional<Book> book = bookService.findById(id);
        if(!book.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found.");
        return ResponseEntity.status(HttpStatus.OK).body(book.get());
    }

    @Transactional
    @DeleteMapping("/{id}")
    @Operation(tags = {"Books"},
            operationId = "DeleteBookById",
            description = "This method deletes a book by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request Success", content = {
                    @Content(mediaType = "MESSAGE/STRING", schema = @Schema(example = "Book deleted successfully."))}),
            @ApiResponse(responseCode = "404", description = "Response Not Found", content =
            @Content(mediaType = "MESSAGE/STRING", schema = @Schema(example = "Book not found.")))})
    public ResponseEntity<Object> deleteById(@PathVariable(value="id") UUID id){
        Optional<Book> book = bookService.findById(id);
        if(!book.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found.");
        for(Author author: book.get().getAuthors()){
            author.getBooks().remove(book);
            authorService.save(author);
        }
        bookService.delete(book.get());
        return ResponseEntity.status(HttpStatus.OK).body("Book deleted successfully.");
    }

    @PutMapping("/{id}")
    @Operation(tags = {"Books"},
            operationId = "PutBookById",
            description = "This method updates a book by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Request Success - The request has been fulfilled and has resulted in one or more new resources being created.", content = {
                    @Content(mediaType = "APPLICATION/JSON", schema = @Schema(implementation = Book.class))}),
            @ApiResponse(responseCode = "409", description = "Conflict", content = {
                    @Content(mediaType = "MESSAGE/STRING", schema = @Schema(example = "Conflict: Registered Book."))}),
            @ApiResponse(responseCode = "404", description = "Response Not Found", content =
            @Content(mediaType = "MESSAGE/STRING", schema = @Schema(example = "Book not found.")))})
    public ResponseEntity<Object> updateBook(@PathVariable(value="id") UUID id,
                                            @RequestBody @Valid BookDto bookDto){
        Optional<Book> bookOptional = bookService.findById(id);
        if(!bookOptional.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found.");
        Book book = new Book();
        BeanUtils.copyProperties(bookDto, book, "authors");
        book.setId(bookOptional.get().getId());
        book.setRegistrationDate(bookOptional.get().getRegistrationDate());

        return getObjectResponseEntity(bookDto, book);
    }

    private ResponseEntity<Object> getObjectResponseEntity(@RequestBody @Valid BookDto bookDto, Book book) {
        for(UUID authorId: bookDto.getAuthors()){
            Optional<Author> authorOptional = authorService.findById(authorId);
            if(!authorOptional.isPresent())
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Unregistered Author.");
            else
                book.getAuthors().add(authorOptional.get());
        }
        book = bookService.save(book);

        for(Author author: book.getAuthors()) {
            author.getBooks().add(book);
            authorService.save(author);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(book);
    }
}