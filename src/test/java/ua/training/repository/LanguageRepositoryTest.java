package ua.training.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import ua.training.model.Language;

import java.util.NoSuchElementException;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class LanguageRepositoryTest {

    @Autowired
    private LanguageRepository languageRepository;

    private Language language1;
    private Language language2;
    private Language language3;

    @Before
    public void setUp() {
        language1 = new Language("uk");
        language2 = new Language("en");
        language3 = new Language("de");

        languageRepository.save(language1);
        languageRepository.save(language2);
        languageRepository.save(language3);
    }

    @After
    public void tearDown() {
        languageRepository.delete(language1);
        languageRepository.delete(language2);
        languageRepository.delete(language3);
    }

    @Test
    public void findByExistsName() {
        Language actual1 = languageRepository.findByName("uk")
                .orElseThrow(() -> new NoSuchElementException("There is no such language in database"));
        Language actual2 = languageRepository.findByName("en")
                .orElseThrow(() -> new NoSuchElementException("There is no such language in database"));
        Language actual3 = languageRepository.findByName("de")
                .orElseThrow(() -> new NoSuchElementException("There is no such language in database"));

        assertEquals(language1.getName(), actual1.getName());
        assertEquals(language2.getName(), actual2.getName());
        assertEquals(language3.getName(), actual3.getName());
    }

    @Test(expected = NoSuchElementException.class)
    public void findByNoExistsName() {
        languageRepository.findByName("ru")
                .orElseThrow(() -> new NoSuchElementException("There is no such language in database"));
    }
}