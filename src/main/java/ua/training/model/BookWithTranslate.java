package ua.training.model;

/**
 * A class that represents the book with its translate
 *
 * @author Yaroslav Koroliuk
 */
public class BookWithTranslate {
    private Book book;
    private BookTranslate bookTranslate;

    /**
     * Constructor - creation of a new book with its translate
     * @param book - a book
     * @param bookTranslate - a book's translate in some language
     */
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
