package ua.training.model;

import javax.persistence.*;

/**
 * The class that represents a book's translate in some language
 *
 * @author Yaroslav Koroliuk
 */
@Entity
@Table(name = "bookTranslate")
public class BookTranslate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Language language;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(length = 1000, nullable = false)
    private String description;

    @Column(length = 30, nullable = false)
    private String languageOfBook;

    @Column(length = 50, nullable = false)
    private String editionName;

    @Column(length = 200, nullable = false)
    private String authorsString;

    public BookTranslate() {
    }

    /**
     * The class that represents a builder pattern for an BookTranslate class
     */
    public static class Builder {
        private long id;
        private Book book;
        private Language language;
        private String title;
        private String description;
        private String languageOfBook;
        private String editionName;
        private String authorsString;

        public BookTranslate.Builder id(long id) {
            this.id = id;
            return this;
        }

        public BookTranslate.Builder book(Book book) {
            this.book = book;
            return this;
        }

        public BookTranslate.Builder language(Language language) {
            this.language = language;
            return this;
        }

        public BookTranslate.Builder title(String title) {
            this.title = title;
            return this;
        }

        public BookTranslate.Builder description(String description) {
            this.description = description;
            return this;
        }

        public BookTranslate.Builder languageOfBook(String languageOfBook) {
            this.languageOfBook = languageOfBook;
            return this;
        }

        public BookTranslate.Builder editionName(String editionName) {
            this.editionName = editionName;
            return this;
        }

        public BookTranslate.Builder authorsString(String authorsString) {
            this.authorsString = authorsString;
            return this;
        }

        /**
         * The method that creates a new translate from a builder
         *
         * @return - a created translate
         */
        public BookTranslate build() {
            return new BookTranslate(this);
        }
    }

    /**
     * Constructor - creation of a new translate from a builder
     *
     * @param builder - translate builder
     * @see BookTranslate#BookTranslate()
     */
    public BookTranslate(BookTranslate.Builder builder) {
        this.id = builder.id;
        this.book = builder.book;
        this.language = builder.language;
        this.title = builder.title;
        this.description = builder.description;
        this.languageOfBook = builder.languageOfBook;
        this.editionName = builder.editionName;
        this.authorsString = builder.authorsString;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLanguageOfBook() {
        return languageOfBook;
    }

    public void setLanguageOfBook(String languageOfBook) {
        this.languageOfBook = languageOfBook;
    }

    public String getEditionName() {
        return editionName;
    }

    public void setEditionName(String editionName) {
        this.editionName = editionName;
    }

    public String getAuthorsString() {
        return authorsString;
    }

    public void setAuthorsString(String authorsString) {
        this.authorsString = authorsString;
    }
}
