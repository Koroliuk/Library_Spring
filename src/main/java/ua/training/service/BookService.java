package ua.training.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.training.model.Book;
import ua.training.model.BookTranslate;
import ua.training.model.BookWithTranslate;
import ua.training.model.Language;
import ua.training.repository.BookRepository;
import ua.training.repository.BookTranslateRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Class that represents a book service
 */
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

    /**
     * The method that deletes the book and its translates
     * @param bookId - a book id
     */
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

    /**
     * The method that finds the book with its translate in specified language
     * @param id - a book id
     * @param language a language
     * @return - a book with translate object
     */
    @Transactional
    public BookWithTranslate findByIdLocated(long id, Language language) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new NoSuchElementException("There is no such book"));
        BookTranslate bookTranslate = bookTranslateRepository.findByBookAndLanguage(book, language)
                .orElseThrow(() -> new NoSuchElementException("There is no such book translate"));
        return new BookWithTranslate(book, bookTranslate);
    }

    /**
     * The method that finds a page of books with its located translates
     * @param pageNo - a page number
     * @param pageSize - a page size
     * @param language - a language
     * @return - a list of books with translates
     */
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

    /**
     * The method that finds a page of books with its located translates that sorted in some way
     * that defines by <b>sortBy<b/> and <b>sortType</b> properties
     * @param pageNo - a page number
     * @param pageSize - a page size
     * @param language - a language
     * @param sortBy - a parameter that indicates which field to sort by
     * @param sortType - a type of sorting
     * @return - a list of books with translates
     */
    @Transactional
    public List<BookWithTranslate> findPaginatedAndLocatedWithSortByAndSortType(int pageNo, int pageSize, Language language,
                                                                                String sortBy, String sortType) {
        Pageable pageable = getPageable(pageNo, pageSize, sortBy, sortType);
        List<BookWithTranslate> bookWithTranslateList = new ArrayList<>();
        if (sortBy.equals("title") || sortBy.equals("editionName") || sortBy.equals("authorsString")) {
            Page<BookTranslate> pagedResult = bookTranslateRepository.findAllByLanguage(language, pageable);
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

    /**
     * The method that finds by key words a page of books with its located translates that sorted in some way
     * that defines by <b>sortBy<b/> and <b>sortType</b> properties
     * @param keyWords - a search key words
     * @param language - a language
     * @param pageNo - a page number
     * @param pageSize - a page size
     * @param sortBy - a parameter that indicates which field to sort by
     * @param sortType - a type of sorting
     * @return - a list of books with translates
     */
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
                    page = bookTranslateRepository.findAllByKeyWordAndLanguageOrderByDateDesc(keyWords, language.getId(),
                            pageable);
                } else  {
                    page = bookTranslateRepository.findAllByKeyWordAndLanguageOrderByDate(keyWords, language.getId(),
                            pageable);
                }
                return getBookWithTranslates(page);
            }
        }
    }

    public int getAmountOfBooks() {
        List<Book> books = (List<Book>) bookRepository.findAll();
        return books.size();
    }

    public int getAmountOfBooksByKeyWords(String keyWords, Language language) {
        List<BookTranslate> bookTranslates = bookTranslateRepository.findAllByKeyWordAndLanguage(keyWords, language.getId());
        return bookTranslates.size();
    }

    /**
     * The method that creates a pageable depending on <b>sortBy<b/> and <b>sortType</b> properties
     * @param pageNo - a page number
     * @param pageSize - a page size
     * @param sortBy - a parameter that indicates which field to sort by
     * @param sortType - a type of sorting
     * @return - a created pageable
     */
    private Pageable getPageable(int pageNo, int pageSize, String sortBy,  String sortType) {
        Pageable pageable;
        if (sortType.trim().equals("dec")) {
            pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
        } else {
            pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        }
        return pageable;
    }

    /**
     * The method that returns list of books with translates from page
     * @param page - a page
     * @return - a list of books
     */
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
