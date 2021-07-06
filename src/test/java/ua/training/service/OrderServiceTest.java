package ua.training.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;
import ua.training.model.*;
import ua.training.model.enums.OrderStatus;
import ua.training.model.enums.Role;
import ua.training.repository.BookRepository;
import ua.training.repository.BookTranslateRepository;
import ua.training.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookTranslateRepository bookTranslateRepository;

    @Mock
    private OrderRepository orderRepository;

    private User user;
    private Language language1;
    private Language language2;
    private Book book1;
    private Book book2;
    private BookTranslate book1TranslateUa;
    private BookTranslate book1TranslateEn;
    private BookTranslate book2TranslateUa;
    private BookTranslate book2TranslateEn;
    private Order order11;
    private Order order12;
    private Order order13;

    @Before
    public void setUp() {
        language1 = new Language("uk");
        language2 = new Language("en");
        user = new User.Builder()
                .login("user1")
                .password("password1")
                .isBlocked(false)
                .role(Role.READER)
                .build();
        book1 = new Book.Builder()
                .publicationDate(LocalDate.now())
                .price(new BigDecimal(120))
                .amount(20)
                .build();
        book2 = new Book.Builder()
                .publicationDate(LocalDate.now())
                .price(new BigDecimal(10))
                .amount(100)
                .build();
        book1TranslateUa = new BookTranslate.Builder()
                .title("Книга1")
                .book(book1)
                .authorsString("Автор1")
                .description("Опис1")
                .language(language1)
                .editionName("Видання")
                .languageOfBook("Українська")
                .build();
        book2TranslateUa = new BookTranslate.Builder()
                .title("Книга2")
                .book(book2)
                .authorsString("Автор2")
                .description("Опис2")
                .language(language1)
                .editionName("Видання")
                .languageOfBook("Українська")
                .build();
        book1TranslateEn = new BookTranslate.Builder()
                .title("Book1")
                .book(book1)
                .authorsString("Author1")
                .description("Description1")
                .language(language2)
                .editionName("Edition")
                .languageOfBook("Українська")
                .build();
        book2TranslateEn = new BookTranslate.Builder()
                .title("Book2")
                .book(book2)
                .authorsString("Author2")
                .description("Description2")
                .language(language2)
                .editionName("Edition")
                .languageOfBook("Українська")
                .build();
        order11 = new Order.Builder()
                .user(user)
                .book(book1)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusYears(1))
                .orderStatus(OrderStatus.RECEIVED)
                .build();
        order12 = new Order.Builder()
                .user(user)
                .book(book2)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusYears(1))
                .orderStatus(OrderStatus.RECEIVED)
                .build();
        order13 = new Order.Builder()
                .user(user)
                .book(book2)
                .startDate(LocalDate.now().minusYears(2))
                .endDate(LocalDate.now().minusYears(1))
                .orderStatus(OrderStatus.APPROVED)
                .build();
    }

    @Test
    public void addOrder() {
        orderService.addOrder(order11);

        verify(orderRepository).save(order11);
    }

    @Test
    public void approveOrder() {
        long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order12));

        orderService.approveOrder(1L);

        verify(orderRepository).save(order12);
    }

    @Test
    public void approveOrderWithNoExistsId() {
        long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> orderService.approveOrder(1L));
    }

    @Test
    public void cancelOrder() {
        long orderId = 1L;
        int amountOld = book2.getAmount();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order13));

        orderService.cancelOrder(orderId);

        verify(bookRepository).save(order13.getBook());
        verify(orderRepository).save(order13);
        assertEquals(amountOld+1, book2.getAmount());
    }

    @Test
    public void cancelOrderWithNoExistsId() {
        long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> orderService.cancelOrder(orderId));
    }

    @Test
    public void checkUserOrders() {
        List<Order> orders = Arrays.asList(order11, order12, order13);
        when(orderRepository.findAllByUser(user)).thenReturn(orders);

        orderService.checkUserOrders(user);

        verify(orderRepository).save(order13);
    }

    @Test
    public void deleteById() {
        long orderId = 1L;
        int amountOld = book2.getAmount();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order13));

        orderService.deleteById(orderId);

        verify(bookRepository).save(order13.getBook());
        verify(orderRepository).delete(order13);
        assertEquals(amountOld+1, book2.getAmount());
    }

    @Test
    public void deleteByWithNoExistsId() {
        long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> orderService.deleteById(orderId));
    }

    @Test
    public void findAllByOrderStatus() {
        Pageable pageable = PageRequest.of(0, 2);
        List<Order> expected = Arrays.asList(order11, order12);
        Page<Order> page = new PageImpl<>(expected, pageable, expected.size());
        when(orderRepository.findAllByOrderStatus(eq(OrderStatus.RECEIVED), any(Pageable.class))).thenReturn(page);
        when(bookTranslateRepository.findByBookAndLanguage(book1, language1)).thenReturn(Optional.of(book1TranslateUa));
        when(bookTranslateRepository.findByBookAndLanguage(book2, language1)).thenReturn(Optional.of(book2TranslateUa));

        List<Order> actual = orderService.findAllByOrderStatus(OrderStatus.RECEIVED, 0, 2, language1);

        assertEquals(expected, actual);
    }

    @Test
    public void findAllByOrderStatusWithNoExistsTranslate() {
        Pageable pageable = PageRequest.of(0, 2);
        List<Order> expected = Arrays.asList(order11, order12);
        Page<Order> page = new PageImpl<>(expected, pageable, expected.size());
        when(orderRepository.findAllByOrderStatus(eq(OrderStatus.RECEIVED), any(Pageable.class))).thenReturn(page);
        when(bookTranslateRepository.findByBookAndLanguage(book1, language1)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class ,
                () -> orderService.findAllByOrderStatus(OrderStatus.RECEIVED, 0, 2, language1));
    }

    @Test
    public void findAllByUserAndOrderStatus() {
        Pageable pageable = PageRequest.of(0, 2);
        List<Order> expected = Arrays.asList(order11, order12);
        Page<Order> page = new PageImpl<>(expected, pageable, expected.size());
        when(orderRepository.findAllByUserAndOrderStatus(eq(user), eq(OrderStatus.RECEIVED), any(Pageable.class)))
                .thenReturn(page);
        when(bookTranslateRepository.findByBookAndLanguage(book1, language2)).thenReturn(Optional.of(book1TranslateEn));
        when(bookTranslateRepository.findByBookAndLanguage(book2, language2)).thenReturn(Optional.of(book2TranslateEn));

        List<Order> actual = orderService.findAllByUserAndOrderStatus(user, OrderStatus.RECEIVED, 0, 2, language2);

        assertEquals(expected, actual);
    }

    @Test
    public void findAllByUserAndOrderStatusWithNoExistsTranslate() {
        Pageable pageable = PageRequest.of(0, 2);
        List<Order> expected = Arrays.asList(order11, order12);
        Page<Order> page = new PageImpl<>(expected, pageable, expected.size());
        when(orderRepository.findAllByUserAndOrderStatus(eq(user), eq(OrderStatus.RECEIVED), any(Pageable.class)))
                .thenReturn(page);
        when(bookTranslateRepository.findByBookAndLanguage(book1, language2)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class ,
                () -> orderService.findAllByUserAndOrderStatus(user, OrderStatus.RECEIVED, 0, 2, language2));
    }

    @Test
    public void findAllByUserAnd2OrderStatus() {
        Pageable pageable = PageRequest.of(0, 3);
        List<Order> expected = Arrays.asList(order11, order12, order13);
        Page<Order> page = new PageImpl<>(expected, pageable, expected.size());
        when(orderRepository.findAllByUserAndOrderStatusOrOrderStatus(eq(user.getId()), eq(OrderStatus.RECEIVED.toString()),
                eq(OrderStatus.APPROVED.toString()), any(Pageable.class))).thenReturn(page);
        when(bookTranslateRepository.findByBookAndLanguage(book1, language2)).thenReturn(Optional.of(book1TranslateEn));
        when(bookTranslateRepository.findByBookAndLanguage(book2, language2)).thenReturn(Optional.of(book2TranslateEn));

        List<Order> actual = orderService.findAllByUserAnd2OrderStatus(user, OrderStatus.RECEIVED, OrderStatus.APPROVED,
                0, 3, language2);

        assertEquals(expected, actual);
    }

    @Test
    public void findAllByUserAnd2OrderStatusWithNoExistsTranslate() {
        Pageable pageable = PageRequest.of(0, 3);
        List<Order> expected = Arrays.asList(order11, order12, order13);
        Page<Order> page = new PageImpl<>(expected, pageable, expected.size());
        when(orderRepository.findAllByUserAndOrderStatusOrOrderStatus(eq(user.getId()), eq(OrderStatus.RECEIVED.toString()),
                eq(OrderStatus.APPROVED.toString()), any(Pageable.class))).thenReturn(page);
        when(bookTranslateRepository.findByBookAndLanguage(book1, language2)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> orderService.findAllByUserAnd2OrderStatus(user, OrderStatus.RECEIVED, OrderStatus.APPROVED,
                0, 3, language2));
    }

    @Test
    public void getAmountByOrderStatus() {
        List<Order> expected = Arrays.asList(order11, order12);
        when(orderRepository.findAllByOrderStatus(OrderStatus.RECEIVED)).thenReturn(expected);

        int actual = orderService.getAmountByOrderStatus(OrderStatus.RECEIVED);

        assertEquals(expected.size(), actual);
    }

    @Test
    public void getAmountByUserAndOrderStatus() {
        List<Order> expected = Arrays.asList(order11, order12);
        when(orderRepository.findAllByUserAndOrderStatus(user, OrderStatus.RECEIVED)).thenReturn(expected);

        int actual = orderService.getAmountByUserAndOrderStatus(user, OrderStatus.RECEIVED);

        assertEquals(expected.size(), actual);
    }

    @Test
    public void getAmountByUserAnd2OrderStatus() {
        List<Order> expected = Arrays.asList(order11, order12, order13);
        when(orderRepository.findAllByUserAndOrderStatusOrOrderStatus(user.getId(), OrderStatus.RECEIVED.toString(),
                OrderStatus.APPROVED.toString())).thenReturn(expected);

        int actual = orderService.getAmountByUserAnd2OrderStatus(user, OrderStatus.RECEIVED, OrderStatus.APPROVED);

        assertEquals(expected.size(), actual);

    }
}