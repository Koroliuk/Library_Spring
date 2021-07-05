package ua.training.converter;

import ua.training.dto.BookDto;
import ua.training.dto.BookTranslateDto;
import ua.training.model.Book;
import ua.training.model.BookTranslate;
import ua.training.model.Language;

public class BookConverter {

    public BookTranslateDto getDtoFromBookTranslate(BookTranslate bookTranslate) {
        BookTranslateDto bookTranslateDto = new BookTranslateDto();
        bookTranslateDto.setTitle(bookTranslate.getTitle());
        bookTranslateDto.setAuthorsString(bookTranslate.getAuthorsString());
        bookTranslateDto.setDescription(bookTranslate.getDescription());
        bookTranslateDto.setBookLanguage(bookTranslate.getLanguageOfBook());
        bookTranslateDto.setEdition(bookTranslate.getEditionName());
        return bookTranslateDto;
    }

    public BookDto getDtoFromBook(Book book) {
        return new BookDto(book.getPublicationDate(), book.getPrice(), book.getAmount());
    }

    public BookTranslate getBookTranslateFromDto(BookTranslateDto bookTranslateDto, Language language, Book book) {
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

    public Book getBookFromDto(BookDto bookDto) {
        return new Book.Builder()
                .publicationDate(bookDto.getPublicationDate())
                .price(bookDto.getPrice())
                .amount(bookDto.getAmount())
                .build();
    }
}
