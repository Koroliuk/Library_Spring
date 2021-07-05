package ua.training.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.training.model.entity.Language;
import ua.training.model.service.BookService;
import ua.training.model.service.LanguageService;

@Controller
public class SearchController {

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
        int amountBooksOnPage = 4;
        int amountOfBookPages;
        if (sortBy == null || sortBy.trim().equals("")) {
            sortBy = "id";
        }
        if (sortType == null || sortType.trim().equals("")) {
            sortType = "asc";
        }
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
        return "search";
    }
}
