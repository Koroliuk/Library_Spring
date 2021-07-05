package ua.training.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.training.model.BookTranslate;
import ua.training.model.Language;
import ua.training.model.Order;
import ua.training.model.User;
import ua.training.model.enums.OrderStatus;
import ua.training.repository.BookTranslateRepository;
import ua.training.repository.OrderRepository;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final BookTranslateRepository bookTranslateRepository;

    public OrderService(OrderRepository orderRepository, BookTranslateRepository bookTranslateRepository) {
        this.orderRepository = orderRepository;
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
        orderRepository.save(order);
    }

    @Transactional
    public void checkUserOrders(User user) {
        List<Order> orderList = orderRepository.findAllByUser(user);
        for (Order order : orderList) {
            LocalDate now = LocalDate.now();
            int amountOfDays = Period.between(order.getEndDate(), now).getDays();
            if (amountOfDays > 0) {
                order.setOrderStatus(OrderStatus.OVERDUE);
                orderRepository.save(order);
            }
        }
    }

    public void deleteById(long id) {
        orderRepository.deleteById(id);
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

    @Transactional
    public List<Order> findAllByUserAnd2OrderStatus(User user, OrderStatus orderStatus1, OrderStatus orderStatus2,
                                                    int pageNo, int pageSize, Language language) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by("id"));
        Page<Order> pagedResult = orderRepository.findAllByUserAndOrderStatusOrOrderStatus(user, orderStatus1,
                orderStatus2, paging);
        List<Order> orders = pagedResult.toList();
        for (Order order : orders) {
            BookTranslate bookTranslate = bookTranslateRepository.findByBookAndLanguage(order.getBook(), language)
                    .orElseThrow(() -> new NoSuchElementException("There is no such translate"));
            order.setBookTranslate(bookTranslate);
        }
        return orders;
    }

    public int getAmountByOrderStatus(OrderStatus orderStatus) {
        Iterable<Order> orders = orderRepository.findAllByOrderStatus(orderStatus);
        int result = 0;
        for (Order ignored : orders) {
            result++;
        }
        return result;
    }

    public int getAmountByUserAndOrderStatus(User user, OrderStatus orderStatus) {
        Iterable<Order> orders = orderRepository.findAllByUserAndOrderStatus(user, orderStatus);
        int result = 0;
        for (Order ignored : orders) {
            result++;
        }
        return result;
    }

    public int getAmountByUserAnd2OrderStatus(User user, OrderStatus orderStatus1, OrderStatus orderStatus2) {
        Iterable<Order> orders = orderRepository.findAllByUserAndOrderStatusOrOrderStatus(user, orderStatus1,
                orderStatus2);
        int result = 0;
        for (Order ignored : orders) {
            result++;
        }
        return result;
    }
}
