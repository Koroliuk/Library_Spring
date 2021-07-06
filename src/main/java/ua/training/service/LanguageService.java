package ua.training.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import ua.training.model.Language;
import ua.training.repository.LanguageRepository;

import javax.annotation.PostConstruct;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Class that represents a language service
 */
@Service
public class LanguageService {

    private final LanguageRepository languageRepository;

    @Autowired
    public LanguageService(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

    public Optional<Language> findByName(String name) {
        return languageRepository.findByName(name);
    }

    /**
     * The method that calls once and adds Ukrainian and English languages
     *  to the database
     */
//    @PostConstruct
//    private void initLanguages() {
//        Language uk = new Language("uk");
//        languageRepository.save(uk);
//
//        Language en = new Language("en");
//        languageRepository.save(en);
//    }

    /**
     * The method that returns a current language from LocaleContextHolder
     * @return - a current language
     */
    public Language getCurrentLanguage() {
        Locale locale = LocaleContextHolder.getLocale();
        return findByName(locale.getLanguage())
                .orElseThrow(() -> new NoSuchElementException("There is no such language"));
    }
}
