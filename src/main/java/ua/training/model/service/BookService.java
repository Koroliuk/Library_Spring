package ua.training.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.training.model.entity.*;
import ua.training.model.repository.BookRepository;
import ua.training.model.repository.BookTranslateRepository;
import ua.training.model.repository.UserRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookTranslateRepository bookTranslateRepository;

    @Autowired
    public BookService(BookRepository bookRepository, BookTranslateRepository bookTranslateRepository) {
        this.bookRepository = bookRepository;
        this.bookTranslateRepository = bookTranslateRepository;
    }

    public void addBook(Book book) {
        bookRepository.save(book);
    }

    public List<BookWithTranslate> findPaginatedAndLocated(int pageNo, int pageSize, Language language) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by("id"));
        Page<Book> pagedResult = bookRepository.findAll(paging);
        List<Book> books = pagedResult.toList();
        List<BookWithTranslate> bookWithTranslateList = new ArrayList<>();
        for (Book book : books) {
            if (language.getName().equals("en")) {
                BigDecimal priceUAN = book.getPrice();
                book.setPrice(priceUAN.divide(new BigDecimal(30), 2));
            }
            BookTranslate bookTranslate = bookTranslateRepository.findByBookAndLanguage(book, language).orElseThrow(() -> new RuntimeException("There is no such translate"));
            BookWithTranslate bookWithTranslate = new BookWithTranslate(book, bookTranslate);
            bookWithTranslateList.add(bookWithTranslate);
        }
        return bookWithTranslateList;
    }

    public List<BookWithTranslate> findPaginatedAndLocatedWithSortByAndSortType(int pageNo, int pageSize, Language language, String sortBy, String sortType) {
        if (sortBy == null || sortBy.trim().equals("")) {
            sortBy = "id";
        }
        if (sortType == null || sortType.trim().equals("")) {
            sortType = "asc";
        }
        Pageable paging;
        if (sortType.trim().equals("dec")) {
            paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
        } else {
            paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        }
        Page<Book> pagedResult = bookRepository.findAll(paging);
        List<Book> books = pagedResult.toList();
        List<BookWithTranslate> bookWithTranslateList = new ArrayList<>();
        for (Book book : books) {
            BookTranslate bookTranslate = bookTranslateRepository.findByBookAndLanguage(book, language).orElseThrow(() -> new RuntimeException("There is no such translate"));
            BookWithTranslate bookWithTranslate = new BookWithTranslate(book, bookTranslate);
            bookWithTranslateList.add(bookWithTranslate);
        }
        return bookWithTranslateList;
    }

    public Optional<Book> findById(long id) {
        return bookRepository.findById(id);
    }

    public int getAmountOfBooks() {
        AtomicInteger amount = new AtomicInteger();
        bookRepository.findAll().forEach((p) -> amount.getAndIncrement());
        return Integer.parseInt(amount.toString());
    }

    @Transactional
    public void deleteBookAndTranslatesByBookId(long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("There is no book with such id"));
        bookTranslateRepository.deleteAllByBook(book);
        bookRepository.delete(book);
    }

    public void updateBook(Book book) {
        bookRepository.save(book);
    }

    public BookWithTranslate findByIdLocated(long id, Language language) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new RuntimeException("There is no such book"));
        BookTranslate bookTranslate = bookTranslateRepository.findByBookAndLanguage(book, language)
                .orElseThrow(() -> new RuntimeException("There is no such book translate"));
        return new BookWithTranslate(book, bookTranslate);
    }

    public int getAmountOfBooksByKeyWords(String keyWords, Language language) {
        AtomicInteger amount = new AtomicInteger();
        bookTranslateRepository
                .findAllByTitleContainsOrAuthorsStringContainsIgnoreCaseAndLanguage(keyWords, keyWords, language).forEach((p) -> amount.getAndIncrement());
        return Integer.parseInt(amount.toString());
    }

    public List<BookWithTranslate> findPaginatedAndLocatedByKeyWords(String keyWords, Language language, int pageNo,
                                                                     int pageSize, String sortBy, String sortType) {
        if (sortBy == null || sortBy.trim().equals("")) {
            sortBy = "id";
        }
        if (sortType == null || sortType.trim().equals("")) {
            sortType = "asc";
        }
        Pageable paging;
        if (sortType.trim().equals("dec")) {
            paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
        } else {
            paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        }
        Page<BookTranslate> page = bookTranslateRepository.findAllByTitleContainsOrAuthorsStringContainsIgnoreCaseAndLanguage(keyWords,
                keyWords, language, paging);
        List<BookTranslate> books = page.toList();
        List<BookWithTranslate> bookWithTranslateList = new ArrayList<>();
        for (BookTranslate bookTranslate : books) {
            long bookId = bookTranslate.getBook().getId();
            Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("There is no such book"));
            BookWithTranslate bookWithTranslate = new BookWithTranslate(book, bookTranslate);
            bookWithTranslateList.add(bookWithTranslate);
        }
        return bookWithTranslateList;
    }
}
