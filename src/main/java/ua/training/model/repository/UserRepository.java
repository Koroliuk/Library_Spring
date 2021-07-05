package ua.training.model.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ua.training.model.entity.User;
import ua.training.model.entity.enums.Role;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long> {
    Optional<User> findByLogin(String login);

    Page<User> findAllByRole(Role role, Pageable paging);

    List<User> findAllByRole(Role role);
}
