package ua.training.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ua.training.model.dto.BookDto;
import ua.training.model.dto.BookTranslateContainerDto;
import ua.training.model.dto.BookTranslateDto;
import ua.training.model.dto.UserDto;
import ua.training.model.entity.Book;
import ua.training.model.entity.BookTranslate;
import ua.training.model.entity.Language;
import ua.training.model.entity.User;
import ua.training.model.entity.enums.Role;
import ua.training.model.service.BookService;
import ua.training.model.service.BookTranslateService;
import ua.training.model.service.LanguageService;
import ua.training.model.service.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping(value = "/admin")
public class AdminController {

    private final UserService userService;
    private final BookService bookService;
    private final BookTranslateService bookTranslateService;
    private final LanguageService languageService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminController(UserService userService, BookService bookService, BookTranslateService bookTranslateService,
                           LanguageService languageService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.bookService = bookService;
        this.bookTranslateService = bookTranslateService;
        this.languageService = languageService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping(value = "/home")
    public String getAdminHomePage(Model model, @RequestParam int tab, @RequestParam int page) {
        int amountUsersOnPage = 5;
        int amountOfUserPages = (userService.getAmountOfUsers() - 1) / amountUsersOnPage + 1;
        int amountBooksOnPage = 4;
        int amountOfBookPages = (bookService.getAmountOfBooks() - 1) / amountBooksOnPage + 1;
        model.addAttribute("tab", tab);
        model.addAttribute("amountOfUserPages", amountOfUserPages);
        model.addAttribute("amountOfBookPages", amountOfBookPages);
        Locale locale = LocaleContextHolder.getLocale();
        Language language = languageService.findByName(locale.getLanguage())
                .orElseThrow(() -> new RuntimeException("There is no such language"));
        if (tab == 1) {
            model.addAttribute("users", userService.findPaginated(page - 1, amountUsersOnPage));
            model.addAttribute("books", bookService.findPaginatedAndLocated(0, amountBooksOnPage, language));
        } else if (tab == 2) {
            model.addAttribute("books", bookService.findPaginatedAndLocated(page - 1, amountBooksOnPage,
                    language));
            model.addAttribute("users", userService.findPaginated(0, amountUsersOnPage));
        } else {
            return "redirect:/error";
        }
        return "/user/admin/home";
    }

    @GetMapping(value = "/addBook")
    public String getAddBookPage(Model model, @RequestParam(required = false) boolean successCreation) {
        model.addAttribute("action", "add");
        model.addAttribute("book", new BookDto());
        List<BookTranslateDto> bookTranslateDtoList = new ArrayList<>();
        bookTranslateDtoList.add(new BookTranslateDto());
        bookTranslateDtoList.add(new BookTranslateDto());
        BookTranslateContainerDto containerDto = new BookTranslateContainerDto(bookTranslateDtoList);
        model.addAttribute("container", containerDto);
        if (successCreation) {
            model.addAttribute("successCreation", true);
        } else {
            model.addAttribute("successCreation", false);
        }
        return "/user/admin/bookForm";
    }

    @PostMapping(value = "/addBook")
    public String addBook(@Valid @ModelAttribute("book") BookDto bookDto,
                          @Valid @ModelAttribute("list") BookTranslateContainerDto containerDto,
                          BindingResult bindingResult,
                          Model model) {
        if (bindingResult.hasErrors()) {
//            model.addAttribute("id", null);
            model.addAttribute("action", "add");
            model.addAttribute("book", bookDto);
            model.addAttribute("container", containerDto);
            return "/user/admin/bookForm";
        }
        BookTranslateDto bookTranslateDtoUa = containerDto.getDtoList().get(0);
        BookTranslateDto bookTranslateDtoEn = containerDto.getDtoList().get(1);
        Language uk = languageService.findByName("uk").orElseThrow(() -> new RuntimeException("kjh"));
        Language en = languageService.findByName("en").orElseThrow(() -> new RuntimeException("kjh"));
        Book book = getBookFromDto(bookDto);
        BookTranslate bookTranslateUa = getBookTranslateFromDto(bookTranslateDtoUa, uk, book);
        BookTranslate bookTranslateEn = getBookTranslateFromDto(bookTranslateDtoEn, en, book);
        String titleUa = bookTranslateUa.getTitle();
        String authorsStringUa = bookTranslateUa.getAuthorsString();
        String titleEn = bookTranslateEn.getTitle();
        String authorsStringEn = bookTranslateEn.getAuthorsString();
        if (bookTranslateService.findByTitleAndAuthorsString(titleUa, authorsStringUa).size() > 0
                || bookTranslateService.findByTitleAndAuthorsString(titleEn, authorsStringEn).size() > 0) {
//            model.addAttribute("id", null);
            model.addAttribute("action", "add");
            model.addAttribute("book", bookDto);
            model.addAttribute("container", containerDto);
            model.addAttribute("actionError", true);
            return "/user/admin/bookForm";
        }
        bookService.addBook(book);
        bookTranslateService.addBookTranslate(bookTranslateUa);
        bookTranslateService.addBookTranslate(bookTranslateEn);
        return "redirect:/admin/addBook?successCreation=true";
    }

    @GetMapping(value = "/deleteBook")
    public String deleteBook(@RequestParam long id) {
        bookService.deleteBookAndTranslatesByBookId(id);
        return "redirect:/admin/home?tab=2&page=1";
    }

    @GetMapping(value = "/editBook")
    public String getEditPage(@RequestParam long id, @RequestParam(required = false) boolean successEditing,
                              Model model) {
        Book book = bookService.findById(id).orElseThrow(() -> new RuntimeException("There is no boo with such id"));
        Language uk = languageService.findByName("uk")
                .orElseThrow(() -> new RuntimeException("There is no such language at database"));
        Language en = languageService.findByName("en")
                .orElseThrow(() -> new RuntimeException("There is no such language at database"));
        BookTranslate bookTranslateUa = bookTranslateService.findByBookAndLanguage(book, uk)
                .orElseThrow(() -> new RuntimeException("There is no such translate"));
        BookTranslate bookTranslateEn = bookTranslateService.findByBookAndLanguage(book, en)
                .orElseThrow(() -> new RuntimeException("There is no such translate"));
        model.addAttribute("id", id);
        model.addAttribute("action", "edit");
        BookDto bookDto = getDtoFromBook(book);
        model.addAttribute("book", bookDto);
        List<BookTranslateDto> bookTranslateDtoList = new ArrayList<>();
        bookTranslateDtoList.add(getDtoFromBookTranslate(bookTranslateUa));
        bookTranslateDtoList.add(getDtoFromBookTranslate(bookTranslateEn));
        BookTranslateContainerDto containerDto = new BookTranslateContainerDto(bookTranslateDtoList);
        model.addAttribute("container", containerDto);
        if (successEditing) {
            model.addAttribute("successEditing", true);
        } else {
            model.addAttribute("successEditing", false);
        }
        return "/user/admin/bookForm";
    }

    @PostMapping(value = "/editBook")
    public String editBook(@RequestParam long id, @Valid @ModelAttribute("book") BookDto bookDto,
                          @Valid @ModelAttribute("list") BookTranslateContainerDto containerDto,
                          BindingResult bindingResult,
                          Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("id", id);
            model.addAttribute("action", "add");
            model.addAttribute("book", bookDto);
            model.addAttribute("container", containerDto);
            return "/user/admin/bookForm";
        }
        BookTranslateDto bookTranslateDtoUa = containerDto.getDtoList().get(0);
        BookTranslateDto bookTranslateDtoEn = containerDto.getDtoList().get(1);
        Language uk = languageService.findByName("uk")
                .orElseThrow(() -> new RuntimeException("There is no such language at database"));
        Language en = languageService.findByName("en")
                .orElseThrow(() -> new RuntimeException("There is no such language at database"));
        Book book = getBookFromDto(bookDto);
        BookTranslate bookTranslateUa = getBookTranslateFromDto(bookTranslateDtoUa, uk, book);
        BookTranslate bookTranslateEn = getBookTranslateFromDto(bookTranslateDtoEn, en, book);
        String titleUa = bookTranslateUa.getTitle();
        String titleEn = bookTranslateEn.getTitle();
        String authorsStringUa = bookTranslateUa.getAuthorsString();
        String authorsStringEn = bookTranslateEn.getAuthorsString();
        if (bookTranslateService.findByTitleAndAuthorsString(titleUa, authorsStringUa).size() > 0
                || bookTranslateService.findByTitleAndAuthorsString(titleEn, authorsStringEn).size() > 0) {
            model.addAttribute("id", id);
            model.addAttribute("action", "add");
            model.addAttribute("book", bookDto);
            model.addAttribute("container", containerDto);
            model.addAttribute("actionError", true);
            return "/user/admin/bookForm";
        }
        Book oldBook = bookService.findById(id).orElseThrow(() -> new RuntimeException("There is no such book"));
        book.setId(oldBook.getId());
        bookService.updateBook(book);
        BookTranslate oldUa = bookTranslateService.findByBookAndLanguage(book, uk)
                .orElseThrow(() -> new RuntimeException("There is no book translate"));
        BookTranslate oldEn = bookTranslateService.findByBookAndLanguage(book, en)
                .orElseThrow(() -> new RuntimeException("There is no book translate"));
        bookTranslateUa.setId(oldUa.getId());
        bookTranslateEn.setId(oldEn.getId());
        bookTranslateService.updateBookTranslate(bookTranslateUa);
        bookTranslateService.updateBookTranslate(bookTranslateEn);
        return "redirect:/admin/editBook?successEditing=true";
    }

    @GetMapping(value = "/blockUser")
    public String blockUser(@RequestParam long id) {
        User user = userService.findById(id).orElseThrow(() -> new RuntimeException("There is on such user"));
        user.setBlocked(true);
        userService.update(user);
        return "redirect:/admin/home?tab=1&page=1";
    }

    @GetMapping(value = "/unblockUser")
    public String unblockUser(@RequestParam long id) {
        User user = userService.findById(id).orElseThrow(() -> new RuntimeException("There is on such user"));
        user.setBlocked(false);
        userService.update(user);
        return "redirect:/admin/home?tab=1&page=1";
    }

    @GetMapping(value = "/deleteLibrarian")
    public String deleteLibrarian(@RequestParam long id) {
        userService.deleteById(id);
        return "redirect:/admin/home?tab=1&page=1";
    }

    @GetMapping(value = "/addLibrarian")
    public String getAddLibrarianPage(Model model, @RequestParam(required = false) boolean successCreation) {
        model.addAttribute("user", new UserDto());
        if (successCreation) {
            model.addAttribute("successCreation", true);
        } else {
            model.addAttribute("successCreation", false);
        }
        return "/user/admin/librarianForm";
    }

    @PostMapping(value = "addLibrarian")
    public String addLibrarian(@Valid @ModelAttribute("user") UserDto userDto, Model model) {
        User user = new User.Builder()
                .login(userDto.getLogin())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .role(Role.LIBRARIAN)
                .isBlocked(false)
                .build();
        if (userService.findByLogin(userDto.getLogin()).isPresent()) {
            model.addAttribute("createError", true);
            return "/user/admin/librarianForm";
        }
        userService.singUpUser(user);
        return "redirect:/admin/addLibrarian?successCreation=true";
    }

    private BookTranslateDto getDtoFromBookTranslate(BookTranslate bookTranslate) {
        BookTranslateDto bookTranslateDto = new BookTranslateDto();
        bookTranslateDto.setTitle(bookTranslate.getTitle());
        bookTranslateDto.setAuthorsString(bookTranslate.getAuthorsString());
        bookTranslateDto.setDescription(bookTranslate.getDescription());
        bookTranslateDto.setBookLanguage(bookTranslate.getLanguageOfBook());
        bookTranslateDto.setEdition(bookTranslate.getEditionName());
        return  bookTranslateDto;
    }

    private BookDto getDtoFromBook(Book book) {
        return new BookDto(book.getPublicationDate(), book.getPrice(), book.getAmount());
    }

    private BookTranslate getBookTranslateFromDto(BookTranslateDto bookTranslateDto, Language language, Book book) {
        return new BookTranslate.Builder()
                .book(book)
                .language(language)
                .title(bookTranslateDto.getTitle())
                .description(bookTranslateDto.getDescription())
                .languageOfBook(bookTranslateDto.getBookLanguage())
                .editionName(bookTranslateDto.getEdition())
                .authorsString(bookTranslateDto.getAuthorsString())
                .build();
    }

    private Book getBookFromDto(BookDto bookDto) {
        return new Book.Builder()
                .publicationDate(bookDto.getPublicationDate())
                .price(bookDto.getPrice())
                .amount(bookDto.getAmount())
                .build();
    }
}
