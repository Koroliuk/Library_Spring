package ua.training.model.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.training.model.entity.Language;

import java.util.Optional;

@Repository
public interface LanguageRepository extends CrudRepository<Language, Long> {
    Optional<Language> findByName(String name);
}
