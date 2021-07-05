package ua.training.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.training.model.Order;
import ua.training.model.User;
import ua.training.model.enums.OrderStatus;

import java.util.List;

@Repository
public interface OrderRepository extends PagingAndSortingRepository<Order, Long> {
    @Query(value = "SELECT * FROM orders WHERE user_id = ?1 and (order_status = ?2 " +
            "or order_status = ?3)", nativeQuery = true)
    Page<Order> findAllByUserAndOrderStatusOrOrderStatus(long userId, String orderStatus1, String orderStatus2,
                                                         Pageable paging);

    @Query(value = "SELECT * FROM orders WHERE user_id = ?1 and (order_status = ?2 " +
            "or order_status = ?3)", nativeQuery = true)
    List<Order> findAllByUserAndOrderStatusOrOrderStatus(long userId, String orderStatus1, String orderStatus2);

    Page<Order> findAllByUserAndOrderStatus(User user, OrderStatus orderStatus, Pageable paging);

    List<Order> findAllByUserAndOrderStatus(User user, OrderStatus orderStatus);

    List<Order> findAllByUser(User user);

    Page<Order> findAllByOrderStatus(OrderStatus orderStatus, Pageable paging);

    List<Order> findAllByOrderStatus(OrderStatus orderStatus);
}
