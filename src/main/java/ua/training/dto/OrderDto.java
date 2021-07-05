package ua.training.dto;

import org.springframework.format.annotation.DateTimeFormat;
import ua.training.model.BookWithTranslate;
import ua.training.model.User;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class OrderDto {

    @NotNull(message = "{startDate.validation.required}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @FutureOrPresent(message = "{startDate.validation.message}")
    private LocalDate startDate;

    @NotNull(message = "{endDate.validation.required}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @FutureOrPresent(message = "{endDate.validation.message}")
    private LocalDate endDate;

    @NotBlank
    private String orderType;

    private User user;

    private BookWithTranslate bookWithTranslate;

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

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BookWithTranslate getBookWithTranslate() {
        return bookWithTranslate;
    }

    public void setBookWithTranslate(BookWithTranslate bookWithTranslate) {
        this.bookWithTranslate = bookWithTranslate;
    }
}
