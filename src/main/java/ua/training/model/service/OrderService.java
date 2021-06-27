package ua.training.model.service;

import org.springframework.stereotype.Service;
import ua.training.model.entity.Order;
import ua.training.model.repository.OrderRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;


    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void addOrder(Order order) {
        orderRepository.save(order);
    }
}
