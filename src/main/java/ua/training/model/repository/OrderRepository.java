package ua.training.model.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ua.training.model.entity.Order;

@Repository
public interface OrderRepository extends PagingAndSortingRepository<Order, Long> {
}
