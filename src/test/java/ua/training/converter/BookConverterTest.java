package ua.training.converter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringRunner;
import ua.training.dto.BookDto;
import ua.training.model.Book;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class BookConverterTest {

    @InjectMocks
    private BookConverter bookConverter;

    @Test
    public void convertBookIntoDto() {
        Book book = new Book.Builder()
                .publicationDate(LocalDate.now())
                .price(new BigDecimal(120))
                .amount(20)
                .build();
        BookDto expected = new BookDto(book.getPublicationDate(), book.getPrice(), book.getAmount());

        BookDto actual = bookConverter.convertBookIntoDto(book);

        assertEquals(expected.getPublicationDate(), actual.getPublicationDate());
        assertEquals(expected.getAmount(), actual.getAmount());
        assertEquals(expected.getPrice(), actual.getPrice());
    }
}