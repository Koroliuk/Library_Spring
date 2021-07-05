package ua.training.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.training.model.entity.Book;
import ua.training.model.entity.BookTranslate;
import ua.training.model.entity.Language;
import ua.training.model.repository.BookTranslateRepository;

import java.util.List;
import java.util.Optional;

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
