package ua.training.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.training.model.Language;

import java.util.Optional;

@Repository
public interface LanguageRepository extends CrudRepository<Language, Long> {
    Optional<Language> findByName(String name);
}
