package ua.training.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * The class that represents a book
 *
 * @author Yaroslav Koroliuk
 */
@Entity
@Table(name = "book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private LocalDate publicationDate;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private int amount;

    public Book() {
    }

    /**
     * The class that represents a builder pattern for an Book class
     */
    public static class Builder {
        private long id;
        private LocalDate publicationDate;
        private BigDecimal price;
        private int amount;

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder publicationDate(LocalDate publicationDate) {
            this.publicationDate = publicationDate;
            return this;
        }

        public Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Builder amount(int count) {
            this.amount = count;
            return this;
        }

        /**
         * The method that creates a new book from a builder
         *
         * @return - a created book
         */
        public Book build() {
            return new Book(this);
        }
    }

    /**
     * Constructor - creation of a new book from a builder
     *
     * @param builder - book builder
     * @see Book#Book()
     */
    public Book(Builder builder) {
        this.id = builder.id;
        this.publicationDate = builder.publicationDate;
        this.price = builder.price;
        this.amount = builder.amount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int count) {
        this.amount = count;
    }
}
