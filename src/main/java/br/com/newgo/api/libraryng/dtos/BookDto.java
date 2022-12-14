package br.com.newgo.api.libraryng.dtos;

import br.com.newgo.api.libraryng.models.Author;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;

public class BookDto {

    @NotBlank
    private String title;
    @NotBlank
    @Size(max = 13)
    private String isbn;
    @NotBlank
    private String genre;
    @NotNull
    private int quantity;

    @NotEmpty
    private Set<UUID> authors;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Set<UUID> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<UUID> authors) {
        this.authors = authors;
    }


}
