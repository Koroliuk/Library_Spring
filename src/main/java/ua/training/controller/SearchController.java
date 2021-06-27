package ua.training.controller;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.training.model.entity.Language;
import ua.training.model.service.BookService;
import ua.training.model.service.LanguageService;

import java.util.Locale;

@Controller
public class SearchController {

    private final BookService bookService;
    private final LanguageService languageService;

    public SearchController(BookService bookService, LanguageService languageService) {
        this.bookService = bookService;
        this.languageService = languageService;
    }

    @GetMapping(value = "/search")
    public String getSearchPage(Model model, @RequestParam int page) {
        int amountBooksOnPage = 4;
        int amountOfBookPages = (bookService.getAmountOfBooks() - 1) / amountBooksOnPage + 1;
        model.addAttribute("amountOfBookPages", amountOfBookPages);
        Locale locale = LocaleContextHolder.getLocale();
        Language language = languageService.findByName(locale.getLanguage())
                .orElseThrow(() -> new RuntimeException("There is no such language"));
        model.addAttribute("books", bookService.findPaginatedAndLocated(page - 1, amountBooksOnPage, language));
        return "search";
    }
}
