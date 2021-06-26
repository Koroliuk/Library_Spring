package ua.training.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ua.training.model.entity.User;
import ua.training.model.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void singUpUser(User user) {
        userRepository.save(user);
    }

    public Optional<User> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public List<User> findPaginated(int pageNo, int pageSize) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by("id"));
        Page<User> pagedResult = userRepository.findAll(paging);
        return pagedResult.toList();
    }

    public int getAmountOfUsers() {
        AtomicInteger amount = new AtomicInteger();
        userRepository.findAll().forEach((p) -> amount.getAndIncrement());
        return Integer.parseInt(amount.toString());
    }

    public Optional<User> findById(long id) {
        return userRepository.findById(id);
    }

    public void update(User user) {
        userRepository.save(user);
    }

    public void deleteById(long id) {
        userRepository.deleteById(id);
    }
}