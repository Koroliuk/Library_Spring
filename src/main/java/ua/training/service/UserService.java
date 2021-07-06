package ua.training.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ua.training.model.User;
import ua.training.model.enums.Role;
import ua.training.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Class that represents a user service
 */
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

    public void update(User user) {
        userRepository.save(user);
    }

    public Optional<User> findById(long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public List<User> findPaginated(int pageNo, int pageSize) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by("id"));
        Page<User> pagedResult = userRepository.findAll(paging);
        return pagedResult.toList();
    }

    public List<User> findAllByRole(Role role, int pageNo, int pageSize) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by("id"));
        Page<User> pagedResult = userRepository.findAllByRole(role, paging);
        return pagedResult.toList();
    }

    public void deleteById(long id) {
        userRepository.deleteById(id);
    }

    public int getAmountOfUsers() {
        Iterable<User> users = userRepository.findAll();
        int result = 0;
        for (User ignored : users) {
            result++;
        }
        return result;
    }

    public int getAmountByRole(Role role) {
        Iterable<User> users = userRepository.findAllByRole(role);
        int result = 0;
        for (User ignored : users) {
            result++;
        }
        return result;
    }

    /**
     * The method that returns a current user from SecurityContextHolder
     * @return - a current user
     */
    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        return findByLogin(username)
                .orElseThrow(() -> new NoSuchElementException("There is no such user"));
    }
}
