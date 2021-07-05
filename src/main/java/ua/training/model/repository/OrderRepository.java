package ua.training.model.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ua.training.model.entity.Order;
import ua.training.model.entity.User;
import ua.training.model.entity.enums.OrderStatus;

import java.util.List;

@Repository
public interface OrderRepository extends PagingAndSortingRepository<Order, Long> {
    Page<Order> findAllByUserAndOrderStatusOrOrderStatus(User user, OrderStatus orderStatus1, OrderStatus orderStatus2,
                                                         Pageable paging);

    List<Order> findAllByUserAndOrderStatusOrOrderStatus(User user, OrderStatus orderStatus1, OrderStatus orderStatus2);

    Page<Order> findAllByUserAndOrderStatus(User user, OrderStatus orderStatus, Pageable paging);

    List<Order> findAllByUserAndOrderStatus(User user, OrderStatus orderStatus);

    List<Order> findAllByUser(User user);

    Page<Order> findAllByOrderStatus(OrderStatus orderStatus, Pageable paging);

    List<Order> findAllByOrderStatus(OrderStatus orderStatus);
}
