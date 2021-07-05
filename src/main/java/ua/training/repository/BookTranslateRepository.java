package ua.training.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.training.model.Book;
import ua.training.model.BookTranslate;
import ua.training.model.Language;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookTranslateRepository extends CrudRepository<BookTranslate, Long> {
    void deleteAllByBook(Book book);

    List<BookTranslate> findBookTranslatesByTitleAndAuthorsString(String title, String authorsString);

    Optional<BookTranslate> findByBookAndLanguage(Book book, Language language);

    Page<BookTranslate> findAllByLanguage(Language language, Pageable pageable);

    @Query(value = "SELECT * from book_translate WHERE language_id = ?2 AND (title LIKE '%' || ?1 || '%' OR " +
            "authors_string LIKE '%' || ?1 || '%')", nativeQuery = true)
    Page<BookTranslate> findAllByKeyWordAndLanguage(String keyWord, long languageId, Pageable pageable);

    @Query(value = "SELECT * from book_translate WHERE language_id = ?2 AND (title LIKE '%' || ?1 || '%' OR " +
            "authors_string LIKE '%' || ?1 || '%')", nativeQuery = true)
    List<BookTranslate> findAllByKeyWordAndLanguage(String keyWord, long languageId);

    @Query(value = "SELECT * from book_translate WHERE language_id = ?2 AND (title LIKE '%' || ?1 || '%' OR " +
            "authors_string LIKE '%' || ?1 || '%') ORDER BY (SELECT id FROM book " +
            "WHERE book.id = book_translate.book_id)", nativeQuery = true)
    Page<BookTranslate> findAllByKeyWordAndLanguageOrderByBookId(String keyWord, long languageId, Pageable pageable);

    @Query(value = "SELECT * from book_translate WHERE language_id = ?2 AND (title LIKE '%' || ?1 || '%' OR " +
            "authors_string LIKE '%' || ?1 || '%') ORDER BY (SELECT publication_date FROM book " +
            "WHERE book.id = book_translate.book_id)", nativeQuery = true)
    Page<BookTranslate> findAllByKeyWordAndLanguageOrderByDate(String keyWord, long languageId, Pageable pageable);

    @Query(value = "SELECT * from book_translate WHERE language_id = ?2 AND (title LIKE '%' || ?1 || '%' OR " +
            "authors_string LIKE '%' || ?1 || '%') ORDER BY (SELECT publication_date FROM book " +
            "WHERE book.id = book_translate.book_id) DESC ", nativeQuery = true)
    Page<BookTranslate> findAllByKeyWordAndLanguageOrderByDateDesc(String keyWords, long languageId, Pageable pageable);
}
