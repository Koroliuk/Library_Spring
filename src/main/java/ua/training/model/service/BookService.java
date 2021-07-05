package ua.training.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.training.model.entity.*;
import ua.training.model.repository.BookRepository;
import ua.training.model.repository.BookTranslateRepository;

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
        System.out.println(sortBy + " " + sortType);
        Pageable paging;
        if (sortType.trim().equals("dec")) {
            paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
        } else {
            paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        }
        List<BookWithTranslate> bookWithTranslateList = new ArrayList<>();
        if (sortBy.equals("title") || sortBy.equals("editionName") || sortBy.equals("authorsString")) {
            Page<BookTranslate> pagedResult = bookTranslateRepository.findAllByLanguage(language, paging);
            System.out.println("get page");
            List<BookTranslate> bookTranslates = pagedResult.toList();
            for (BookTranslate bookTranslate : bookTranslates) {
                Book book = bookRepository.findById(bookTranslate.getBook().getId()).orElseThrow(() -> new RuntimeException("There is on such book"));
                BookWithTranslate bookWithTranslate = new BookWithTranslate(book, bookTranslate);
                bookWithTranslateList.add(bookWithTranslate);
            }
        } else {
            Page<Book> pagedResult = bookRepository.findAll(paging);
            List<Book> books = pagedResult.toList();
            for (Book book : books) {
                BookTranslate bookTranslate = bookTranslateRepository.findByBookAndLanguage(book, language).orElseThrow(() -> new RuntimeException("There is no such translate"));
                BookWithTranslate bookWithTranslate = new BookWithTranslate(book, bookTranslate);
                bookWithTranslateList.add(bookWithTranslate);
            }
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
                .findAllByKeyWordAndLanguage(keyWords, language.getId()).forEach((p) -> amount.getAndIncrement());
        return Integer.parseInt(amount.toString());
    }

    public List<BookWithTranslate> findPaginatedAndLocatedByKeyWords(String keyWords, Language language, int pageNo,
                                                                     int pageSize, String sortBy, String sortType) {
        if (sortBy.equals("editionName")) {
            sortBy = "edition_name";
        }
        if (sortBy.equals("authorsString")) {
            sortBy = "authors_string";
        }
        Pageable paging;
        if (sortType.trim().equals("dec")) {
            paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
        } else {
            paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        }
        List<BookWithTranslate> bookWithTranslateList = new ArrayList<>();
        if (sortBy.equals("title") || sortBy.equals("edition_name") || sortBy.equals("authors_string")) {
            Page<BookTranslate> page = bookTranslateRepository.findAllByKeyWordAndLanguage(keyWords, language.getId(), paging);
            List<BookTranslate> bookTranslates = page.toList();
            for (BookTranslate bookTranslate : bookTranslates) {
                long bookId = bookTranslate.getBook().getId();
                Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("There is no such book"));
                BookWithTranslate bookWithTranslate = new BookWithTranslate(book, bookTranslate);
                bookWithTranslateList.add(bookWithTranslate);
            }
        } else {
            if (sortBy.equals("id")) {
                paging = PageRequest.of(pageNo, pageSize);
                Page<BookTranslate> page = bookTranslateRepository.findAllByKeyWordAndLanguageOrderByBookId(keyWords, language.getId(), paging);
                List<BookTranslate> bookTranslates = page.toList();
                for (BookTranslate bookTranslate : bookTranslates) {
                    long bookId = bookTranslate.getBook().getId();
                    Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("There is no such book"));
                    BookWithTranslate bookWithTranslate = new BookWithTranslate(book, bookTranslate);
                    bookWithTranslateList.add(bookWithTranslate);
                }
            } else {
                paging = PageRequest.of(pageNo, pageSize);
                Page<BookTranslate> page = bookTranslateRepository.findAllByKeyWordAndLanguageOrderByDate(keyWords, language.getId(), paging);
                List<BookTranslate> bookTranslates = page.toList();
                for (BookTranslate bookTranslate : bookTranslates) {
                    long bookId = bookTranslate.getBook().getId();
                    Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("There is no such book"));
                    BookWithTranslate bookWithTranslate = new BookWithTranslate(book, bookTranslate);
                    bookWithTranslateList.add(bookWithTranslate);
                }
            }
        }
        return bookWithTranslateList;
    }
}
