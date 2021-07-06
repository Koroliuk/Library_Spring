package ua.training.converter;

import ua.training.dto.BookDto;
import ua.training.dto.BookTranslateDto;
import ua.training.model.Book;
import ua.training.model.BookTranslate;
import ua.training.model.Language;

/**
 * Class that represents a converter between book and book dto classes
 */
public class BookConverter {

    /**
     * The method that converts a book translate into its DTO
     * @param bookTranslate - a book translate
     * @return - a resulted DTO
     */
    public BookTranslateDto convertDtoIntoBookTranslate(BookTranslate bookTranslate) {
        BookTranslateDto bookTranslateDto = new BookTranslateDto();
        bookTranslateDto.setTitle(bookTranslate.getTitle());
        bookTranslateDto.setAuthorsString(bookTranslate.getAuthorsString());
        bookTranslateDto.setDescription(bookTranslate.getDescription());
        bookTranslateDto.setBookLanguage(bookTranslate.getLanguageOfBook());
        bookTranslateDto.setEdition(bookTranslate.getEditionName());
        return bookTranslateDto;
    }

    /**
     * The method that converts a book into book DTO
     * @param book - a book
     * @return - a resulted DTO
     */
    public BookDto convertBookIntoDto(Book book) {
        return new BookDto(book.getPublicationDate(), book.getPrice(), book.getAmount());
    }

    /**
     * The method that converts a book translate DTO into a book translate
     * @param bookTranslateDto - a book translate DTO
     * @param language - a language of the translate
     * @param book - a book
     * @return - a book translate
     */
    public BookTranslate convertBookTranslateIntoDto(BookTranslateDto bookTranslateDto, Language language, Book book) {
        return new BookTranslate.Builder()
                .book(book)
                .language(language)
                .title(bookTranslateDto.getTitle())
                .description(bookTranslateDto.getDescription())
                .languageOfBook(bookTranslateDto.getBookLanguage())
                .editionName(bookTranslateDto.getEdition())
                .authorsString(bookTranslateDto.getAuthorsString())
                .build();
    }

    /**
     * The method that converts a book DTI into a book DTO
     * @param bookDto - a book DTO
     * @return - a resulted book
     */
    public Book convertDtoIntoBook(BookDto bookDto) {
        return new Book.Builder()
                .publicationDate(bookDto.getPublicationDate())
                .price(bookDto.getPrice())
                .amount(bookDto.getAmount())
                .build();
    }
}
