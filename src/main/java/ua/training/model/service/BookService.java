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
import java.util.NoSuchElementException;
import java.util.Optional;

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

    public void updateBook(Book book) {
        bookRepository.save(book);
    }

    @Transactional
    public void deleteBookAndTranslatesByBookId(long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NoSuchElementException("There is no book with such id"));
        bookTranslateRepository.deleteAllByBook(book);
        bookRepository.delete(book);
    }

    public Optional<Book> findById(long id) {
        return bookRepository.findById(id);
    }

    @Transactional
    public BookWithTranslate findByIdLocated(long id, Language language) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new NoSuchElementException("There is no such book"));
        BookTranslate bookTranslate = bookTranslateRepository.findByBookAndLanguage(book, language)
                .orElseThrow(() -> new NoSuchElementException("There is no such book translate"));
        return new BookWithTranslate(book, bookTranslate);
    }

    @Transactional
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
            BookTranslate bookTranslate = bookTranslateRepository.findByBookAndLanguage(book, language)
                    .orElseThrow(() -> new NoSuchElementException("There is no such translate"));
            BookWithTranslate bookWithTranslate = new BookWithTranslate(book, bookTranslate);
            bookWithTranslateList.add(bookWithTranslate);
        }
        return bookWithTranslateList;
    }

    @Transactional
    public List<BookWithTranslate> findPaginatedAndLocatedWithSortByAndSortType(int pageNo, int pageSize, Language language,
                                                                                String sortBy, String sortType) {
        Pageable pageable = getPageable(pageNo, pageSize, sortBy, sortType);
        List<BookWithTranslate> bookWithTranslateList = new ArrayList<>();
        if (sortBy.equals("title") || sortBy.equals("editionName") || sortBy.equals("authorsString")) {
            Page<BookTranslate> pagedResult = bookTranslateRepository.findAllByLanguage(language, pageable);
            System.out.println("get page");
            List<BookTranslate> bookTranslates = pagedResult.toList();
            for (BookTranslate bookTranslate : bookTranslates) {
                Book book = bookRepository.findById(bookTranslate.getBook().getId())
                        .orElseThrow(() -> new NoSuchElementException("There is on such book"));
                BookWithTranslate bookWithTranslate = new BookWithTranslate(book, bookTranslate);
                bookWithTranslateList.add(bookWithTranslate);
            }
        } else {
            Page<Book> pagedResult = bookRepository.findAll(pageable);
            List<Book> books = pagedResult.toList();
            for (Book book : books) {
                BookTranslate bookTranslate = bookTranslateRepository.findByBookAndLanguage(book, language)
                        .orElseThrow(() -> new NoSuchElementException("There is no such translate"));
                BookWithTranslate bookWithTranslate = new BookWithTranslate(book, bookTranslate);
                bookWithTranslateList.add(bookWithTranslate);
            }
        }
        return bookWithTranslateList;
    }

    @Transactional
    public List<BookWithTranslate> findPaginatedAndLocatedByKeyWords(String keyWords, Language language, int pageNo,
                                                                     int pageSize, String sortBy, String sortType) {
        if (sortBy.equals("editionName")) {
            sortBy = "edition_name";
        }
        if (sortBy.equals("authorsString")) {
            sortBy = "authors_string";
        }
        Pageable pageable = getPageable(pageNo, pageSize, sortBy, sortType);
        if (sortBy.equals("title") || sortBy.equals("edition_name") || sortBy.equals("authors_string")) {
            Page<BookTranslate> page = bookTranslateRepository.findAllByKeyWordAndLanguage(keyWords, language.getId(),
                    pageable);
            return getBookWithTranslates(page);
        } else {
            if (sortBy.equals("id")) {
                pageable = PageRequest.of(pageNo, pageSize);
                Page<BookTranslate> page = bookTranslateRepository.findAllByKeyWordAndLanguageOrderByBookId(keyWords,
                        language.getId(), pageable);
                return getBookWithTranslates(page);
            } else {
                pageable = PageRequest.of(pageNo, pageSize);
                Page<BookTranslate> page;
                if (sortType.equals("dec")) {
                    page = bookTranslateRepository.findAllByKeyWordAndLanguageOrderByDate(keyWords, language.getId(),
                            pageable);
                } else  {
                    page = bookTranslateRepository.findAllByKeyWordAndLanguageOrderByDateDesc(keyWords, language.getId(),
                            pageable);
                }
                return getBookWithTranslates(page);
            }
        }
    }

    public int getAmountOfBooks() {
        Iterable<Book> books = bookRepository.findAll();
        int result = 0;
        for (Book ignored : books) {
            result++;
        }
        return result;
    }

    public int getAmountOfBooksByKeyWords(String keyWords, Language language) {
        Iterable<BookTranslate> books = bookTranslateRepository.findAllByKeyWordAndLanguage(keyWords, language.getId());
        int result = 0;
        for (BookTranslate ignored : books) {
            result++;
        }
        return result;
    }

    private Pageable getPageable(int pageNo, int pageSize, String sortBy,  String sortType) {
        Pageable pageable;
        if (sortType.trim().equals("dec")) {
            pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
        } else {
            pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        }
        return pageable;
    }

    private List<BookWithTranslate> getBookWithTranslates(Page<BookTranslate> page) {
        List<BookWithTranslate> bookWithTranslateList = new ArrayList<>();
        List<BookTranslate> bookTranslates = page.toList();
        for (BookTranslate bookTranslate : bookTranslates) {
            long bookId = bookTranslate.getBook().getId();
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new NoSuchElementException("There is no such book"));
            BookWithTranslate bookWithTranslate = new BookWithTranslate(book, bookTranslate);
            bookWithTranslateList.add(bookWithTranslate);
        }
        return bookWithTranslateList;
    }
}
