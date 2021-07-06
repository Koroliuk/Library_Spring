package ua.training.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.training.model.Book;
import ua.training.model.BookTranslate;
import ua.training.model.Language;
import ua.training.repository.BookTranslateRepository;

import java.util.List;
import java.util.Optional;

/**
 * Class that represents a book tranlsate service
 */
@Service
public class BookTranslateService {

    private final BookTranslateRepository bookTranslateRepository;

    @Autowired
    public BookTranslateService(BookTranslateRepository bookTranslateRepository) {
        this.bookTranslateRepository = bookTranslateRepository;
    }

    public void addBookTranslate(BookTranslate bookTranslate) {
        bookTranslateRepository.save(bookTranslate);
    }

    public void updateBookTranslate(BookTranslate bookTranslate) {
        bookTranslateRepository.save(bookTranslate);
    }

    public List<BookTranslate> findByTitleAndAuthorsString(String title, String authorsString) {
        return bookTranslateRepository.findBookTranslatesByTitleAndAuthorsString(title, authorsString);
    }

    public Optional<BookTranslate> findByBookAndLanguage(Book book, Language language) {
        return bookTranslateRepository.findByBookAndLanguage(book, language);
    }
}
