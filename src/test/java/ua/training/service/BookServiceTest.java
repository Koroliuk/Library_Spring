package ua.training.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;
import ua.training.model.*;
import ua.training.repository.BookRepository;
import ua.training.repository.BookTranslateRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class BookServiceTest {

    @InjectMocks
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookTranslateRepository bookTranslateRepository;

    private Language language1;
    private Language language2;
    private Book book1;
    private Book book2;
    private BookTranslate book1TranslateUa;
    private BookTranslate book1TranslateEn;
    private BookTranslate book2TranslateUa;
    private BookTranslate book2TranslateEn;

    @Before
    public void setUp() throws Exception {
        language1 = new Language("uk");
        language2 = new Language("en");
        book1 = new Book.Builder()
                .publicationDate(LocalDate.now())
                .price(new BigDecimal(120))
                .amount(20)
                .build();
        book2 = new Book.Builder()
                .publicationDate(LocalDate.now())
                .price(new BigDecimal(10))
                .amount(100)
                .build();
        book1TranslateUa = new BookTranslate.Builder()
                .title("Книга1")
                .book(book1)
                .authorsString("Автор1")
                .description("Опис1")
                .language(language1)
                .editionName("Видання")
                .languageOfBook("Українська")
                .build();
        book2TranslateUa = new BookTranslate.Builder()
                .title("Книга2")
                .book(book2)
                .authorsString("Автор2")
                .description("Опис2")
                .language(language1)
                .editionName("Видання")
                .languageOfBook("Українська")
                .build();
        book1TranslateEn = new BookTranslate.Builder()
                .title("Book1")
                .book(book1)
                .authorsString("Author1")
                .description("Description1")
                .language(language2)
                .editionName("Edition")
                .languageOfBook("Українська")
                .build();
        book2TranslateEn = new BookTranslate.Builder()
                .title("Book2")
                .book(book2)
                .authorsString("Author2")
                .description("Description2")
                .language(language2)
                .editionName("Edition")
                .languageOfBook("Українська")
                .build();
    }

    @Test
    public void addBook() {
        bookService.addBook(book1);

        verify(bookRepository).save(book1);
    }

    @Test
    public void updateBook() {
        bookService.updateBook(book1);

        verify(bookRepository).save(book1);
    }

    @Test
    public void deleteBookAndTranslatesByBookId() {
        long bookId = 1L;
        book1.setId(bookId);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book1));

        bookService.deleteBookAndTranslatesByBookId(bookId);

        verify(bookTranslateRepository).deleteAllByBook(book1);
        verify(bookRepository).delete(book1);
    }

    @Test
    public void deleteBookAndTranslatesByBookId_WithNoExistsTranslate() {
        long bookId = 1L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> bookService.deleteBookAndTranslatesByBookId(bookId));
    }

    @Test
    public void findById() {
        long bookId = 1L;

        bookService.findById(bookId);

        verify(bookRepository).findById(bookId);
    }

    @Test
    public void findByIdLocated() {
        long bookId = 2L;
        BookWithTranslate expected = new BookWithTranslate(book2, book2TranslateUa);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book2));
        when(bookTranslateRepository.findByBookAndLanguage(book2, language1)).thenReturn(Optional.of(book2TranslateUa));

        BookWithTranslate actual = bookService.findByIdLocated(bookId, language1);

        assertEquals(expected.getBook(), actual.getBook());
        assertEquals(expected.getBookTranslate(), actual.getBookTranslate());
    }

    @Test
    public void findByIdLocatedWithNoExistsBook() {
        long bookId = 2L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> bookService.findByIdLocated(bookId, language1));
    }

    @Test
    public void findByIdLocatedWithNoExistsTranslate() {
        long bookId = 2L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book2));
        when(bookTranslateRepository.findByBookAndLanguage(book2, language1)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> bookService.findByIdLocated(bookId, language1));
    }

    @Test
    public void findPaginatedAndLocated() {
        List<Book> books = Arrays.asList(book1, book2);
        List<BookWithTranslate> expected = Arrays.asList(new BookWithTranslate(book1, book1TranslateUa),
                new BookWithTranslate(book2, book2TranslateUa));
        Pageable pageable = PageRequest.of(0, 2);
        Page<Book> page = new PageImpl<>(books, pageable, books.size());
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(bookTranslateRepository.findByBookAndLanguage(book1, language1)).thenReturn(Optional.of(book1TranslateUa));
        when(bookTranslateRepository.findByBookAndLanguage(book2, language1)).thenReturn(Optional.of(book2TranslateUa));

        List<BookWithTranslate> actual = bookService.findPaginatedAndLocated(0, 2, language1);

        assertEquals(expected.get(0).getBook(), actual.get(0).getBook());
        assertEquals(expected.get(0).getBookTranslate(), actual.get(0).getBookTranslate());
        assertEquals(expected.get(1).getBook(), actual.get(1).getBook());
        assertEquals(expected.get(1).getBookTranslate(), actual.get(1).getBookTranslate());
    }

    @Test
    public void findPaginatedAndLocatedWithNoTranslate() {
        List<Book> books = Arrays.asList(book1, book2);
        Pageable pageable = PageRequest.of(0, 2);
        Page<Book> page = new PageImpl<>(books, pageable, books.size());
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(bookTranslateRepository.findByBookAndLanguage(book1, language1)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> bookService.findPaginatedAndLocated(0, 2, language1));
    }

    @Test
    public void findPaginatedAndLocatedWithSortByAndSortType_ForTranslateSortParams() {
        long bookId1 = 1L;
        long bookId2 = 2L;
        book1TranslateEn.getBook().setId(bookId1);
        book2TranslateEn.getBook().setId(bookId2);
        List<BookTranslate> bookTranslates = Arrays.asList(book1TranslateEn, book2TranslateEn);
        List<BookWithTranslate> expected = Arrays.asList(new BookWithTranslate(book1, book1TranslateEn),
                new BookWithTranslate(book2, book2TranslateEn));
        Pageable pageable = PageRequest.of(0, 2);
        Page<BookTranslate> page = new PageImpl<>(bookTranslates, pageable, bookTranslates.size());
        when(bookTranslateRepository.findAllByLanguage(eq(language2), any(Pageable.class))).thenReturn(page);
        when(bookRepository.findById(bookId1)).thenReturn(Optional.of(book1));
        when(bookRepository.findById(bookId2)).thenReturn(Optional.of(book2));

        List<BookWithTranslate> actual = bookService.findPaginatedAndLocatedWithSortByAndSortType(0, 2,
                language2, "title", "dec");

        assertEquals(expected.get(0).getBook(), actual.get(0).getBook());
        assertEquals(expected.get(0).getBookTranslate(), actual.get(0).getBookTranslate());
        assertEquals(expected.get(1).getBook(), actual.get(1).getBook());
        assertEquals(expected.get(1).getBookTranslate(), actual.get(1).getBookTranslate());
    }

    @Test
    public void findPaginatedAndLocatedWithSortByAndSortType_ForBookSortParams() {
        List<Book> books = Arrays.asList(book1, book2);
        List<BookWithTranslate> expected = Arrays.asList(new BookWithTranslate(book1, book1TranslateEn),
                new BookWithTranslate(book2, book2TranslateEn));
        Pageable pageable = PageRequest.of(0, 2);
        Page<Book> page = new PageImpl<>(books, pageable, books.size());
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(bookTranslateRepository.findByBookAndLanguage(book1, language2)).thenReturn(Optional.of(book1TranslateEn));
        when(bookTranslateRepository.findByBookAndLanguage(book2, language2)).thenReturn(Optional.of(book2TranslateEn));

        List<BookWithTranslate> actual = bookService.findPaginatedAndLocatedWithSortByAndSortType(0, 2,
                language2, "id", "asc");

        assertEquals(expected.get(0).getBook(), actual.get(0).getBook());
        assertEquals(expected.get(0).getBookTranslate(), actual.get(0).getBookTranslate());
        assertEquals(expected.get(1).getBook(), actual.get(1).getBook());
        assertEquals(expected.get(1).getBookTranslate(), actual.get(1).getBookTranslate());
    }

    @Test
    public void findPaginatedAndLocatedWithSortByAndSortType_ForTranslateSortParams_WithNoExistsBook() {
        long bookId1 = 1L;
        long bookId2 = 2L;
        book1TranslateEn.getBook().setId(bookId1);
        book2TranslateEn.getBook().setId(bookId2);
        List<BookTranslate> bookTranslates = Arrays.asList(book1TranslateEn, book2TranslateEn);
        Pageable pageable = PageRequest.of(0, 2);
        Page<BookTranslate> page = new PageImpl<>(bookTranslates, pageable, bookTranslates.size());
        when(bookTranslateRepository.findAllByLanguage(eq(language2), any(Pageable.class))).thenReturn(page);
        when(bookRepository.findById(bookId1)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> bookService.findPaginatedAndLocatedWithSortByAndSortType(0, 2,
                language2, "title", "dec"));
    }

    @Test
    public void findPaginatedAndLocatedWithSortByAndSortType_ForBookSortParams_WithNoExistsTranslate() {
        List<Book> books = Arrays.asList(book1, book2);
        Pageable pageable = PageRequest.of(0, 2);
        Page<Book> page = new PageImpl<>(books, pageable, books.size());
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(bookTranslateRepository.findByBookAndLanguage(book1, language2)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> bookService.findPaginatedAndLocatedWithSortByAndSortType(0, 2,
                language2, "id", "asc"));
    }

    @Test
    public void findPaginatedAndLocatedByKeyWords_ForTranslateSortParams() {
        long bookId1 = 1L;
        long bookId2 = 2L;
        book1TranslateEn.getBook().setId(bookId1);
        book2TranslateEn.getBook().setId(bookId2);
        String keyWords = "Book";
        List<BookTranslate> bookTranslates = Arrays.asList(book1TranslateEn, book2TranslateEn);
        List<BookWithTranslate> expected = Arrays.asList(new BookWithTranslate(book1, book1TranslateEn),
                new BookWithTranslate(book2, book2TranslateEn));
        Pageable pageable = PageRequest.of(0, 2);
        Page<BookTranslate> page = new PageImpl<>(bookTranslates, pageable, bookTranslates.size());
        when(bookTranslateRepository.findAllByKeyWordAndLanguage(eq(keyWords), eq(language2.getId()), any(Pageable.class)))
                .thenReturn(page);
        when(bookRepository.findById(bookId1)).thenReturn(Optional.of(book1));
        when(bookRepository.findById(bookId2)).thenReturn(Optional.of(book2));

        List<BookWithTranslate> actual = bookService.findPaginatedAndLocatedByKeyWords(keyWords, language2, 
                0, 2, "title", "dec");

        assertEquals(expected.get(0).getBook(), actual.get(0).getBook());
        assertEquals(expected.get(0).getBookTranslate(), actual.get(0).getBookTranslate());
        assertEquals(expected.get(1).getBook(), actual.get(1).getBook());
        assertEquals(expected.get(1).getBookTranslate(), actual.get(1).getBookTranslate());
    }

    @Test
    public void findPaginatedAndLocatedByKeyWords_ForBookIdSortParam() {
        long bookId1 = 1L;
        long bookId2 = 2L;
        book1TranslateEn.getBook().setId(bookId1);
        book2TranslateEn.getBook().setId(bookId2);
        String keyWords = "Book";
        List<BookTranslate> bookTranslates = Arrays.asList(book1TranslateEn, book2TranslateEn);
        List<BookWithTranslate> expected = Arrays.asList(new BookWithTranslate(book1, book1TranslateEn),
                new BookWithTranslate(book2, book2TranslateEn));
        Pageable pageable = PageRequest.of(0, 2);
        Page<BookTranslate> page = new PageImpl<>(bookTranslates, pageable, bookTranslates.size());
        when(bookTranslateRepository.findAllByKeyWordAndLanguageOrderByBookId(eq(keyWords), eq(language2.getId()), any(Pageable.class)))
                .thenReturn(page);
        when(bookRepository.findById(bookId1)).thenReturn(Optional.of(book1));
        when(bookRepository.findById(bookId2)).thenReturn(Optional.of(book2));

        List<BookWithTranslate> actual = bookService.findPaginatedAndLocatedByKeyWords(keyWords, language2,
                0, 2, "id", "dec");

        assertEquals(expected.get(0).getBook(), actual.get(0).getBook());
        assertEquals(expected.get(0).getBookTranslate(), actual.get(0).getBookTranslate());
        assertEquals(expected.get(1).getBook(), actual.get(1).getBook());
        assertEquals(expected.get(1).getBookTranslate(), actual.get(1).getBookTranslate());
    }

    @Test
    public void findPaginatedAndLocatedByKeyWords_ForDateDescSortParams() {
        long bookId1 = 1L;
        long bookId2 = 2L;
        book1TranslateEn.getBook().setId(bookId1);
        book2TranslateEn.getBook().setId(bookId2);
        String keyWords = "Book";
        List<BookTranslate> bookTranslates = Arrays.asList(book1TranslateEn, book2TranslateEn);
        List<BookWithTranslate> expected = Arrays.asList(new BookWithTranslate(book1, book1TranslateEn),
                new BookWithTranslate(book2, book2TranslateEn));
        Pageable pageable = PageRequest.of(0, 2);
        Page<BookTranslate> page = new PageImpl<>(bookTranslates, pageable, bookTranslates.size());
        when(bookTranslateRepository.findAllByKeyWordAndLanguageOrderByDateDesc(eq(keyWords), eq(language2.getId()), any(Pageable.class)))
                .thenReturn(page);
        when(bookRepository.findById(bookId1)).thenReturn(Optional.of(book1));
        when(bookRepository.findById(bookId2)).thenReturn(Optional.of(book2));

        List<BookWithTranslate> actual = bookService.findPaginatedAndLocatedByKeyWords(keyWords, language2,
                0, 2, "publicationDate", "dec");

        assertEquals(expected.get(0).getBook(), actual.get(0).getBook());
        assertEquals(expected.get(0).getBookTranslate(), actual.get(0).getBookTranslate());
        assertEquals(expected.get(1).getBook(), actual.get(1).getBook());
        assertEquals(expected.get(1).getBookTranslate(), actual.get(1).getBookTranslate());
    }

    @Test
    public void findPaginatedAndLocatedByKeyWords_ForDateAscSortParams() {
        long bookId1 = 1L;
        long bookId2 = 2L;
        book1TranslateEn.getBook().setId(bookId1);
        book2TranslateEn.getBook().setId(bookId2);
        String keyWords = "Book";
        List<BookTranslate> bookTranslates = Arrays.asList(book1TranslateEn, book2TranslateEn);
        List<BookWithTranslate> expected = Arrays.asList(new BookWithTranslate(book1, book1TranslateEn),
                new BookWithTranslate(book2, book2TranslateEn));
        Pageable pageable = PageRequest.of(0, 2);
        Page<BookTranslate> page = new PageImpl<>(bookTranslates, pageable, bookTranslates.size());
        when(bookTranslateRepository.findAllByKeyWordAndLanguageOrderByDate(eq(keyWords), eq(language2.getId()), any(Pageable.class)))
                .thenReturn(page);
        when(bookRepository.findById(bookId1)).thenReturn(Optional.of(book1));
        when(bookRepository.findById(bookId2)).thenReturn(Optional.of(book2));

        List<BookWithTranslate> actual = bookService.findPaginatedAndLocatedByKeyWords(keyWords, language2,
                0, 2, "publicationDate", "asc");

        assertEquals(expected.get(0).getBook(), actual.get(0).getBook());
        assertEquals(expected.get(0).getBookTranslate(), actual.get(0).getBookTranslate());
        assertEquals(expected.get(1).getBook(), actual.get(1).getBook());
        assertEquals(expected.get(1).getBookTranslate(), actual.get(1).getBookTranslate());
    }

    @Test
    public void findPaginatedAndLocatedByKeyWords_WithNoExistsBook() {
        long bookId1 = 1L;
        long bookId2 = 2L;
        book1TranslateEn.getBook().setId(bookId1);
        book2TranslateEn.getBook().setId(bookId2);
        String keyWords = "Book";
        List<BookTranslate> bookTranslates = Arrays.asList(book1TranslateEn, book2TranslateEn);
        Pageable pageable = PageRequest.of(0, 2);
        Page<BookTranslate> page = new PageImpl<>(bookTranslates, pageable, bookTranslates.size());
        when(bookTranslateRepository.findAllByKeyWordAndLanguageOrderByDate(eq(keyWords), eq(language2.getId()), any(Pageable.class)))
                .thenReturn(page);
        when(bookRepository.findById(bookId1)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> bookService.findPaginatedAndLocatedByKeyWords(keyWords, language2,
                0, 2, "publicationDate", "asc"));
    }

    @Test
    public void getAmountOfBooks() {
        List<Book> books = Arrays.asList(book1, book2);
        when(bookRepository.findAll()).thenReturn(books);

        int actual = bookService.getAmountOfBooks();

        assertEquals(books.size(),actual);
    }

    @Test
    public void getAmountOfBooksByKeyWords() {
        String keyWords = "Book";
        List<BookTranslate> bookTranslates = Arrays.asList(book1TranslateEn, book2TranslateEn);
        when(bookTranslateRepository.findAllByKeyWordAndLanguage(keyWords, language2.getId()))
                .thenReturn(bookTranslates);

        int actual = bookService.getAmountOfBooksByKeyWords(keyWords, language2);

        assertEquals(bookTranslates.size(), actual);
    }
}