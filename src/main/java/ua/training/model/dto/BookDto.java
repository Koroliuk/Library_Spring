package ua.training.model.dto;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class BookDto {

    @NotNull(message = "Publication date is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent(message = "Publication date must be past or present")
    private LocalDate publicationDate;

    @NotNull(message = "Price is required")
    @PositiveOrZero(message = "Price must be positive ot zero")
    private BigDecimal price;

    @NotNull(message = "Amount is required")
    @PositiveOrZero(message = "Amount must be positive ot zero")
    private int amount;

    public BookDto() {}

    public BookDto(LocalDate publicationDate, BigDecimal price, int amount) {
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

}
