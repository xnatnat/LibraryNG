package br.com.newgo.api.libraryng.services;

import br.com.newgo.api.libraryng.models.Author;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public interface IAuthorService {

    Author save(Author author);

    boolean existsById(UUID id);

    List<Author> findAll();

    Optional<Author> findById(UUID id);

    Author findByAuthorId(UUID id);

    void delete(Author author);
}
