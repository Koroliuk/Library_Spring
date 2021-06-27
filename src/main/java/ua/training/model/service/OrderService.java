package ua.training.model.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ua.training.model.entity.*;
import ua.training.model.entity.enums.OrderStatus;
import ua.training.model.repository.BookTranslateRepository;
import ua.training.model.repository.OrderRepository;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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

    public List<Order> getApprovedAndOverdueOrdersByUserId(User user, OrderStatus orderStatus1, OrderStatus orderStatus2,
                                                           int pageNo, int pageSize, Language language) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by("id"));
        Page<Order> pagedResult = orderRepository.findAllByUserAndOrderStatusOrOrderStatus(user, orderStatus1, orderStatus2, paging);
        List<Order> orders = pagedResult.toList();
        for (Order order : orders) {
            BookTranslate bookTranslate = bookTranslateRepository.findByBookAndLanguage(order.getBook(), language)
                    .orElseThrow(() -> new RuntimeException("There is no such translate"));
            order.setBookTranslate(bookTranslate);
        }
        return orders;
    }

    public List<Order> getReadingHoleOrders(User user, OrderStatus orderStatus, int pageNo, int pageSize, Language language) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by("id"));
        Page<Order> pagedResult = orderRepository.findAllByUserAndOrderStatus(user, orderStatus, paging);
        List<Order> orders = pagedResult.toList();
        for (Order order : orders) {
            BookTranslate bookTranslate = bookTranslateRepository.findByBookAndLanguage(order.getBook(), language)
                    .orElseThrow(() -> new RuntimeException("There is no such translate"));
            order.setBookTranslate(bookTranslate);
        }
        return orders;
    }

    public int getAmountByUserAnd2OrOrderStatus(User user, OrderStatus approved, OrderStatus overdue) {
        AtomicInteger amount = new AtomicInteger();
        orderRepository.findAllByUserAndOrderStatusOrOrderStatus(user, approved, overdue).forEach((p) -> amount.getAndIncrement());
        return Integer.parseInt(amount.toString());
    }

    public int getAmountByUserAndOrderStatus(User user, OrderStatus orderStatus) {
        AtomicInteger amount = new AtomicInteger();
        orderRepository.findAllByUserAndOrderStatus(user, orderStatus).forEach((p) -> amount.getAndIncrement());
        return Integer.parseInt(amount.toString());
    }

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
}
