package br.com.newgo.api.libraryng.services;

import br.com.newgo.api.libraryng.models.Author;
import br.com.newgo.api.libraryng.repositories.AuthorRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthorService implements IAuthorService {
    final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository){
        this.authorRepository = authorRepository;
    }

    @Override
    @Transactional
    public Author save(Author author) {return authorRepository.save(author);}

    @Override
    public boolean existsById(UUID id) {
        return authorRepository.existsById(id);
    }

    @Override
    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    @Override
    public Optional<Author> findById(UUID id) {
        return authorRepository.findById(id);
    }

    public Author findByAuthorId(UUID id){ return authorRepository.getReferenceById(id);}

    @Override
    public void delete(Author author) {
        authorRepository.delete(author);
    }

}
