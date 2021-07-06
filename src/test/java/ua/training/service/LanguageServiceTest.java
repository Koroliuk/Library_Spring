package ua.training.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.context.junit4.SpringRunner;
import ua.training.model.Language;
import ua.training.repository.LanguageRepository;

import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class LanguageServiceTest {

    @InjectMocks
    private LanguageService languageService;

    @Mock
    private LanguageRepository languageRepository;

    @Test
    public void findByExistsName() {
        String languageName = "pl";
        Language expected = new Language(languageName);
        when(languageRepository.findByName(languageName)).thenReturn(Optional.of(expected));

        Language actual = languageService.findByName(languageName)
                .orElseThrow(() -> new NoSuchElementException("There is no such language in database"));

        verify(languageRepository).findByName(languageName);
        assertEquals(actual, expected);
    }

    @Test
    public void findByNoExistsName() {
        String languageName = "pl";
        when(languageRepository.findByName(languageName)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> languageService.findByName(languageName)
                .orElseThrow(() -> new NoSuchElementException("There is no such language in database")));
    }


    @Test
    public void getCurrentLanguage() {
        MockedStatic<LocaleContextHolder> holderMockedStatic = mockStatic(LocaleContextHolder.class);
        Locale locale = mock(Locale.class);
        String languageName = "uk";
        Language expected = new Language(languageName);
        holderMockedStatic.when(LocaleContextHolder::getLocale).thenReturn(locale);
        when(locale.getLanguage()).thenReturn(languageName);
        when(languageService.findByName(languageName)).thenReturn(Optional.of(expected));

        Language actual = languageService.getCurrentLanguage();

        assertEquals(expected, actual);
    }
}