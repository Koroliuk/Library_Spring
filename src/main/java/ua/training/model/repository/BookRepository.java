package ua.training.model.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ua.training.model.entity.Book;

@Repository
public interface BookRepository extends PagingAndSortingRepository<Book, Long> {
}
