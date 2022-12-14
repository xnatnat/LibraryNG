package br.com.newgo.api.libraryng.services;

import br.com.newgo.api.libraryng.models.Book;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public interface IBookService {

    Book save(Book book);

    boolean existsByIsbn(String isbn);

    List<Book> findAll();

    Optional<Book> findById(UUID id);

    void delete(Book book);
}
