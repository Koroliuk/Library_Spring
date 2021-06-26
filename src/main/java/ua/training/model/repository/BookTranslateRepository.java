package ua.training.model.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.training.model.entity.Book;
import ua.training.model.entity.BookTranslate;
import ua.training.model.entity.Language;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookTranslateRepository extends CrudRepository<BookTranslate, Long> {
    List<BookTranslate> findBookTranslatesByTitleAndAuthorsString(String title, String authorsString);
    Optional<BookTranslate> findByBookAndLanguage(Book book, Language language);
    void deleteAllByBook(Book book);
}
