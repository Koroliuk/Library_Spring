package ua.training.model.entity;

import ua.training.model.entity.enums.OrderStatus;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

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

    public BigDecimal getFine() {
        LocalDate now = LocalDate.now();
        int amountOfDays = Period.between(endDate, now).getDays();
        return book.getPrice().multiply(new
                BigDecimal(amountOfDays)).multiply(BigDecimal.valueOf(0.01));
    }

    public Order() {
    }

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

        public Order build() {
            return new Order(this);
        }
    }

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
