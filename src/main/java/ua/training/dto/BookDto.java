package ua.training.dto;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class BookDto {

    @NotNull(message = "{publicationDate.validation.required}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent(message = "{publicationDate.validation.message1}")
    private LocalDate publicationDate;

    @NotNull(message = "{price.validation.required}")
    @PositiveOrZero(message = "{price.validation.message1}")
    private BigDecimal price;

    @NotNull(message = "{amount.validation.required}")
    @PositiveOrZero(message = "{amount.validation.message1}")
    private Integer amount;

    public BookDto() {
    }

    public BookDto(LocalDate publicationDate, BigDecimal price, Integer amount) {
        this.publicationDate = publicationDate;
        this.price = price;
        this.amount = amount;
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

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

}
