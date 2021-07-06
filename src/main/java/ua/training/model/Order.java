package ua.training.model;

import ua.training.model.enums.OrderStatus;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

/**
 * The class that represents an order with properties <b>user</b>, <b>book</b>, <b>bookTranslate</b>,
 * <b>startDate</b>, <b>endDate</b>, <b>orderStatus</b>
 *
 * @author Yaroslav Koroliuk
 */
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Book book;

    @Transient
    private BookTranslate bookTranslate;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(length = 30, nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    /**
     * The method that calculates a user's fine if the order is overdue
     *
     * @return - fine
     */
    public BigDecimal getFine() {
        if (orderStatus.equals(OrderStatus.OVERDUE)) {
            LocalDate now = LocalDate.now();
            int amountOfDays = Period.between(endDate, now).getDays();
            return book.getPrice().multiply(new
                    BigDecimal(amountOfDays)).multiply(BigDecimal.valueOf(0.01));
        }
        return BigDecimal.ZERO;
    }

    public Order() {
    }

    /**
     * The class that represents a builder pattern for an Order class
     */
    public static class Builder {
        private long id;
        private User user;
        private Book book;
        private LocalDate startDate;
        private LocalDate endDate;
        private OrderStatus orderStatus;

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder book(Book book) {
            this.book = book;
            return this;
        }

        public Builder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder orderStatus(OrderStatus orderStatus) {
            this.orderStatus = orderStatus;
            return this;
        }

        /**
         * The method that creates a new order from a builder
         *
         * @return - a created order
         */
        public Order build() {
            return new Order(this);
        }
    }

    /**
     * Constructor - creation of a new order from a builder
     *
     * @see Order#Order()
     */
    private Order(Builder builder) {
        this.id = builder.id;
        this.user = builder.user;
        this.book = builder.book;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.orderStatus = builder.orderStatus;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public BookTranslate getBookTranslate() {
        return bookTranslate;
    }

    public void setBookTranslate(BookTranslate bookTranslate) {
        this.bookTranslate = bookTranslate;
    }
}
