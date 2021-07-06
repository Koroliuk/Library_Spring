package ua.training.controller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.training.model.Language;
import ua.training.service.BookService;
import ua.training.service.LanguageService;

@Controller
public class SearchController {
    private static final Logger logger = LogManager.getLogger();

    private final BookService bookService;
    private final LanguageService languageService;

    public SearchController(BookService bookService, LanguageService languageService) {
        this.bookService = bookService;
        this.languageService = languageService;
    }

    @RequestMapping(value = "/search", method = {RequestMethod.GET, RequestMethod.POST})
    public String getSearchPage(Model model, @RequestParam int page, @RequestParam(required = false) String keyWords,
                                @RequestParam(required = false) String sortBy,
                                @RequestParam(required = false) String sortType) {
        Language language = languageService.getCurrentLanguage();
        if (sortBy == null || sortBy.trim().equals("")) {
            sortBy = "id";
        }
        if (sortType == null || sortType.trim().equals("")) {
            sortType = "asc";
        }
        int amountBooksOnPage = 4;
        int amountOfBookPages;
        if (keyWords == null || keyWords.trim().equals("")) {
            amountOfBookPages = (bookService.getAmountOfBooks() - 1) / amountBooksOnPage + 1;
            model.addAttribute("amountOfBookPages", amountOfBookPages)
                    .addAttribute("keyWords", keyWords)
                    .addAttribute("books", bookService.findPaginatedAndLocatedWithSortByAndSortType(page - 1,
                            amountBooksOnPage, language, sortBy, sortType));
        } else {
            amountOfBookPages = (bookService.getAmountOfBooksByKeyWords(keyWords, language) - 1) / amountBooksOnPage + 1;
            model.addAttribute("amountOfBookPages", amountOfBookPages)
                    .addAttribute("keyWords", keyWords)
                    .addAttribute("books", bookService.findPaginatedAndLocatedByKeyWords(keyWords, language,
                    page - 1, amountBooksOnPage, sortBy, sortType));
        }
        if (sortBy.trim().equals("")) {
            model.addAttribute("sortBy", "");
        } else {
            model.addAttribute("sortBy", sortBy);
        }
        if (sortType.trim().equals("")) {
            model.addAttribute("sortType", "asc");
        } else {
            model.addAttribute("sortType", sortType);
        }
        model.addAttribute("page", page);
        logger.info(String.format("Search: there was a search with the following parameters: keyWords='%s', sortBy='%s', " +
                "sortType='%s'", keyWords, sortBy, sortType));
        return "search";
    }
}
