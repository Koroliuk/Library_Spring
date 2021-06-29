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
    public String getSearchPage(Model model, @RequestParam int page, @RequestParam(required = false) String keyWords,
                                @RequestParam(required = false) String sortBy,
                                @RequestParam(required = false) String sortType) {
        Locale locale = LocaleContextHolder.getLocale();
        Language language = languageService.findByName(locale.getLanguage())
                .orElseThrow(() -> new RuntimeException("There is no such language"));
        int amountBooksOnPage = 4;
        int amountOfBookPages;
        if (keyWords == null || keyWords.trim().equals("")) {
            amountOfBookPages = (bookService.getAmountOfBooks() - 1) / amountBooksOnPage + 1;
            model.addAttribute("amountOfBookPages", amountOfBookPages);
            model.addAttribute("books", bookService.findPaginatedAndLocatedWithSortByAndSortType(page - 1, amountBooksOnPage, language, sortBy, sortType));
            model.addAttribute("keyWords", "");
        } else {
            amountOfBookPages = (bookService.getAmountOfBooksByKeyWords(keyWords, language) - 1) / amountBooksOnPage + 1;
            model.addAttribute("amountOfBookPages", amountOfBookPages);
            model.addAttribute("books", bookService.findPaginatedAndLocatedByKeyWords(keyWords, language,
                    page - 1, amountBooksOnPage, sortBy, sortType));
            model.addAttribute("keyWords", keyWords);
        }
        if (sortBy == null || sortBy.trim().equals("")) {
            model.addAttribute("sortBy", "");
        } else {
            model.addAttribute("sortBy", sortBy);
        }
        if (sortType == null || sortType.trim().equals("")) {
            model.addAttribute("sortType", "asc");
        } else {
            model.addAttribute("sortType", sortType);
        }
        model.addAttribute("page", page);
        return "search";
    }
}
