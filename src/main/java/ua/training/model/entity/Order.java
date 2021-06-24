package ua.training.model.entity;

import ua.training.model.entity.enums.OrderStatus;

import java.time.LocalDate;

public class Order {
    private long id;
    private User user;
    private Book book;
    private LocalDate startDate;
    private LocalDate endDate;
    private OrderStatus orderStatus;

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
}
