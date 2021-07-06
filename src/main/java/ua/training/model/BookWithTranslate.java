package ua.training.model;

/**
 * The class that represents a book with its translate
 *
 * @author Yaroslav Koroliuk
 */
public class BookWithTranslate {
    private Book book;
    private BookTranslate bookTranslate;

    public BookWithTranslate(Book book, BookTranslate bookTranslate) {
        this.book = book;
        this.bookTranslate = bookTranslate;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public BookTranslate getBookTranslate() {
        return bookTranslate;
    }

    public void setBookTranslate(BookTranslate bookTranslate) {
        this.bookTranslate = bookTranslate;
    }
}
