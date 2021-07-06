package ua.training.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.training.model.*;
import ua.training.model.enums.OrderStatus;
import ua.training.repository.BookRepository;
import ua.training.repository.BookTranslateRepository;
import ua.training.repository.OrderRepository;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Class that represents an order service
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private final BookTranslateRepository bookTranslateRepository;

    public OrderService(OrderRepository orderRepository, BookRepository bookRepository,
                        BookTranslateRepository bookTranslateRepository) {
        this.orderRepository = orderRepository;
        this.bookRepository = bookRepository;
        this.bookTranslateRepository = bookTranslateRepository;
    }

    public void addOrder(Order order) {
        orderRepository.save(order);
    }

    @Transactional
    public void approveOrder(long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("There is no such order"));
        order.setOrderStatus(OrderStatus.APPROVED);
        orderRepository.save(order);
    }

    @Transactional
    public void cancelOrder(long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("There is no such order"));
        order.setOrderStatus(OrderStatus.CANCELED);
        Book book = order.getBook();
        book.setAmount(book.getAmount()+1);
        bookRepository.save(book);
        orderRepository.save(order);
    }

    /**
     * The a method that checks whether orders have expired and changes status accordingly
     * @param user - a user
     */
    @Transactional
    public void checkUserOrders(User user) {
        List<Order> orderList = orderRepository.findAllByUser(user);
        for (Order order : orderList) {
            LocalDate now = LocalDate.now();
            Period period = Period.between(order.getEndDate(), now);
            if (!period.isNegative() && !period.isZero()) {
                order.setOrderStatus(OrderStatus.OVERDUE);
                orderRepository.save(order);
            }
        }
    }

    public void deleteById(long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("There is no such order"));
        Book book = order.getBook();
        book.setAmount(book.getAmount()+1);
        bookRepository.save(book);
        orderRepository.delete(order);
    }

    @Transactional
    public List<Order> findAllByOrderStatus(OrderStatus orderStatus, int pageNo, int pageSize, Language language) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by("id"));
        Page<Order> pagedResult = orderRepository.findAllByOrderStatus(orderStatus, paging);
        List<Order> orders = pagedResult.toList();
        for (Order order : orders) {
            BookTranslate bookTranslate = bookTranslateRepository.findByBookAndLanguage(order.getBook(), language)
                    .orElseThrow(() -> new NoSuchElementException("There is no such translate"));
            order.setBookTranslate(bookTranslate);
        }
        return orders;
    }

    @Transactional
    public List<Order> findAllByUserAndOrderStatus(User user, OrderStatus orderStatus, int pageNo, int pageSize,
                                                   Language language) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by("id"));
        Page<Order> pagedResult = orderRepository.findAllByUserAndOrderStatus(user, orderStatus, paging);
        List<Order> orders = pagedResult.toList();
        for (Order order : orders) {
            BookTranslate bookTranslate = bookTranslateRepository.findByBookAndLanguage(order.getBook(), language)
                    .orElseThrow(() -> new NoSuchElementException("There is no such translate"));
            order.setBookTranslate(bookTranslate);
        }
        return orders;
    }

    /**
     * The method that finds all user orders that have either first or second order status
     * @param user - a user
     * @param orderStatus1 - a first order status
     * @param orderStatus2 - a second order status
     * @param pageNo - a page number
     * @param pageSize - a page size
     * @param language - a language
     * @return - a list of orders
     */
    @Transactional
    public List<Order> findAllByUserAnd2OrderStatus(User user, OrderStatus orderStatus1, OrderStatus orderStatus2,
                                                    int pageNo, int pageSize, Language language) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by("id"));
        Page<Order> pagedResult = orderRepository.findAllByUserAndOrderStatusOrOrderStatus(user.getId(),
                orderStatus1.toString(), orderStatus2.toString(), paging);
        List<Order> orders = pagedResult.toList();
        for (Order order : orders) {
            BookTranslate bookTranslate = bookTranslateRepository.findByBookAndLanguage(order.getBook(), language)
                    .orElseThrow(() -> new NoSuchElementException("There is no such translate"));
            order.setBookTranslate(bookTranslate);
        }
        return orders;
    }

    public int getAmountByOrderStatus(OrderStatus orderStatus) {
        List<Order> orders = orderRepository.findAllByOrderStatus(orderStatus);
        return orders.size();
    }

    public int getAmountByUserAndOrderStatus(User user, OrderStatus orderStatus) {
        List<Order> orders = orderRepository.findAllByUserAndOrderStatus(user, orderStatus);
        return orders.size();
    }

    /**
     * The method that finds amount of user orders that have either first or second order status
     * @param user - a user
     * @param orderStatus1 - a first order status
     * @param orderStatus2 - a second order status
     * @return - a list of orders
     */
    public int getAmountByUserAnd2OrderStatus(User user, OrderStatus orderStatus1, OrderStatus orderStatus2) {
        List<Order> orders = orderRepository.findAllByUserAndOrderStatusOrOrderStatus(user.getId(),
                orderStatus1.toString(), orderStatus2.toString());
        return orders.size();
    }
}
