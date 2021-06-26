package ua.training.model.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.training.model.entity.BookTranslate;

import java.util.List;

@Repository
public interface BookTranslateRepository extends CrudRepository<BookTranslate, Long> {
    List<BookTranslate> findBookTranslatesByTitleAndAuthorsString(String title, String authorsString);
}
