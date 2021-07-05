package ua.training.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ua.training.model.Book;

@Repository
public interface BookRepository extends PagingAndSortingRepository<Book, Long> {
}
