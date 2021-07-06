package ua.training.controller.user;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ua.training.converter.BookConverter;
import ua.training.validation.BookWithTranslateValidator;
import ua.training.dto.BookDto;
import ua.training.dto.BookTranslateContainerDto;
import ua.training.dto.BookTranslateDto;
import ua.training.dto.UserDto;
import ua.training.model.Book;
import ua.training.model.BookTranslate;
import ua.training.model.Language;
import ua.training.model.User;
import ua.training.model.enums.Role;
import ua.training.service.BookService;
import ua.training.service.BookTranslateService;
import ua.training.service.LanguageService;
import ua.training.service.UserService;

import javax.validation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping(value = "/admin")
public class AdminController {
    private static final Logger logger = LogManager.getLogger();

    private final static BookConverter converter = new BookConverter();
    private final static BookWithTranslateValidator validator = new BookWithTranslateValidator();

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
        int amountBooksOnPage = 4;
        int amountOfUserPages = (userService.getAmountOfUsers() - 1) / amountUsersOnPage + 1;
        int amountOfBookPages = (bookService.getAmountOfBooks() - 1) / amountBooksOnPage + 1;
        Language language = languageService.getCurrentLanguage();
        if (tab == 1) {
            model.addAttribute("users", userService.findPaginated(page - 1, amountUsersOnPage))
                    .addAttribute("books", bookService.findPaginatedAndLocated(0, amountBooksOnPage, language));
        } else if (tab == 2) {
            model.addAttribute("books", bookService.findPaginatedAndLocated(page - 1, amountBooksOnPage, language))
                    .addAttribute("users", userService.findPaginated(0, amountUsersOnPage));
        } else {
            logger.info("Request the admin home page with incorrect parameters");
            return "redirect:/error";
        }
        model.addAttribute("tab", tab)
                .addAttribute("currPage", page)
                .addAttribute("amountOfUserPages", amountOfUserPages)
                .addAttribute("amountOfBookPages", amountOfBookPages);
        logger.info("Redirect to the admin home page");
        return "/user/admin/home";
    }

    @GetMapping(value = "/addBook")
    public String getAddBookPage(Model model, @RequestParam(required = false) boolean successCreation) {
        List<BookTranslateDto> bookTranslateDtoList = new ArrayList<>();
        bookTranslateDtoList.add(new BookTranslateDto());
        bookTranslateDtoList.add(new BookTranslateDto());
        BookTranslateContainerDto containerDto = new BookTranslateContainerDto(bookTranslateDtoList);
        model.addAttribute("action", "add")
                .addAttribute("book", new BookDto())
                .addAttribute("container", containerDto);
        if (successCreation) {
            model.addAttribute("successCreation", true);
        } else {
            model.addAttribute("successCreation", false);
        }
        logger.info("Admin | Redirect to the add book page");
        return "/user/admin/bookForm";
    }

    @PostMapping(value = "/addBook")
    public String addBook(@Valid @ModelAttribute("book") BookDto bookDto, BindingResult bindingResult,
                          @ModelAttribute("list") BookTranslateContainerDto containerDto,
                          Model model) {
        if (bindingResult.hasErrors() || !validator.validate(containerDto)) {
            model.addAttribute("validationError", true)
                    .addAttribute("action", "add")
                    .addAttribute("book", bookDto)
                    .addAttribute("container", containerDto);
            logger.info("Admin | Add book: invalid input data");
            return "/user/admin/bookForm";
        }
        BookTranslateDto bookTranslateDtoUa = containerDto.getDtoList().get(0);
        BookTranslateDto bookTranslateDtoEn = containerDto.getDtoList().get(1);
        Language uk = languageService.findByName("uk")
                .orElseThrow(() -> new NoSuchElementException("There is no such language"));
        Language en = languageService.findByName("en")
                .orElseThrow(() -> new NoSuchElementException("There is no such language"));
        Book book = converter.getBookFromDto(bookDto);
        BookTranslate bookTranslateUa = converter.getBookTranslateFromDto(bookTranslateDtoUa, uk, book);
        BookTranslate bookTranslateEn = converter.getBookTranslateFromDto(bookTranslateDtoEn, en, book);
        String titleUa = bookTranslateUa.getTitle();
        String authorsStringUa = bookTranslateUa.getAuthorsString();
        String titleEn = bookTranslateEn.getTitle();
        String authorsStringEn = bookTranslateEn.getAuthorsString();
        if (bookTranslateService.findByTitleAndAuthorsString(titleUa, authorsStringUa).size() > 0
                || bookTranslateService.findByTitleAndAuthorsString(titleEn, authorsStringEn).size() > 0) {
            model.addAttribute("action", "add")
                    .addAttribute("book", bookDto)
                    .addAttribute("container", containerDto)
                    .addAttribute("actionError", true);
            logger.info(String.format("Admin | Add book: a book with such parameters already exists: titleUa='%s', titleEn='%s', " +
                    "authorsUa='%s', authorsEn='%s'", titleUa, titleEn, authorsStringUa, authorsStringEn));
            return "/user/admin/bookForm";
        }
        bookService.addBook(book);
        bookTranslateService.addBookTranslate(bookTranslateUa);
        bookTranslateService.addBookTranslate(bookTranslateEn);
        logger.info(String.format("Admin | Add book: a book with such parameters successfully added to database: titleUa='%s', titleEn='%s', " +
                "authorsUa='%s', authorsEn='%s'", titleUa, titleEn, authorsStringUa, authorsStringEn));
        return "redirect:/admin/addBook?successCreation=true";
    }

    @GetMapping(value = "/deleteBook")
    public String deleteBook(@RequestParam long id) {
        bookService.deleteBookAndTranslatesByBookId(id);
        logger.info(String.format("Admin | Delete book: a book with id='%d' was successfully deleted", id));
        return "redirect:/admin/home?tab=2&page=1";
    }

    @GetMapping(value = "/editBook")
    public String getEditPage(@RequestParam long id, @RequestParam(required = false) boolean successEditing,
                              Model model) {
        Book book = bookService.findById(id)
                .orElseThrow(() -> new NoSuchElementException("There is no book with such id"));
        Language uk = languageService.findByName("uk")
                .orElseThrow(() -> new NoSuchElementException("There is no such language at database"));
        Language en = languageService.findByName("en")
                .orElseThrow(() -> new NoSuchElementException("There is no such language at database"));
        BookTranslate bookTranslateUa = bookTranslateService.findByBookAndLanguage(book, uk)
                .orElseThrow(() -> new NoSuchElementException("There is no such translate"));
        BookTranslate bookTranslateEn = bookTranslateService.findByBookAndLanguage(book, en)
                .orElseThrow(() -> new NoSuchElementException("There is no such translate"));
        BookDto bookDto = converter.getDtoFromBook(book);
        List<BookTranslateDto> bookTranslateDtoList = new ArrayList<>();
        bookTranslateDtoList.add(converter.getDtoFromBookTranslate(bookTranslateUa));
        bookTranslateDtoList.add(converter.getDtoFromBookTranslate(bookTranslateEn));
        BookTranslateContainerDto containerDto = new BookTranslateContainerDto(bookTranslateDtoList);
        model.addAttribute("action", "edit")
                .addAttribute("id", id)
                .addAttribute("book", bookDto)
                .addAttribute("container", containerDto);
        if (successEditing) {
            model.addAttribute("successEditing", true);
        } else {
            model.addAttribute("successEditing", false);
        }
        logger.info("Admin | Redirect to the edit book page");
        return "/user/admin/bookForm";
    }

    @PostMapping(value = "/editBook")
    public String editBook(@RequestParam long id, @Valid @ModelAttribute("book") BookDto bookDto, BindingResult bindingResult,
                           @Valid @ModelAttribute("list") BookTranslateContainerDto containerDto,
                           Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("id", id)
                    .addAttribute("action", "add")
                    .addAttribute("book", bookDto)
                    .addAttribute("container", containerDto);
            logger.info("Admin | Edit book: invalid input data");
            return "/user/admin/bookForm";
        }
        BookTranslateDto bookTranslateDtoUa = containerDto.getDtoList().get(0);
        BookTranslateDto bookTranslateDtoEn = containerDto.getDtoList().get(1);
        Language uk = languageService.findByName("uk")
                .orElseThrow(() -> new NoSuchElementException("There is no such language at database"));
        Language en = languageService.findByName("en")
                .orElseThrow(() -> new NoSuchElementException("There is no such language at database"));
        Book book = converter.getBookFromDto(bookDto);
        BookTranslate bookTranslateUa = converter.getBookTranslateFromDto(bookTranslateDtoUa, uk, book);
        BookTranslate bookTranslateEn = converter.getBookTranslateFromDto(bookTranslateDtoEn, en, book);
        String titleUa = bookTranslateUa.getTitle();
        String titleEn = bookTranslateEn.getTitle();
        String authorsStringUa = bookTranslateUa.getAuthorsString();
        String authorsStringEn = bookTranslateEn.getAuthorsString();
        if (bookTranslateService.findByTitleAndAuthorsString(titleUa, authorsStringUa).size() > 1
                || bookTranslateService.findByTitleAndAuthorsString(titleEn, authorsStringEn).size() > 1) {
            model.addAttribute("id", id)
                    .addAttribute("action", "add")
                    .addAttribute("book", bookDto)
                    .addAttribute("container", containerDto)
                    .addAttribute("actionError", true);
            logger.info(String.format("Admin | Edit book: a book with one couple of such parameters already exists: " +
                    "titleUa='%s', titleEn='%s', authorsUa='%s', authorsEn='%s'", titleUa, titleEn, authorsStringUa,
                    authorsStringEn));
            return "/user/admin/bookForm";
        }
        Book oldBook = bookService.findById(id).orElseThrow(() -> new NoSuchElementException("There is no such book"));
        book.setId(oldBook.getId());
        bookService.updateBook(book);
        BookTranslate oldUa = bookTranslateService.findByBookAndLanguage(book, uk)
                .orElseThrow(() -> new NoSuchElementException("There is no book translate"));
        BookTranslate oldEn = bookTranslateService.findByBookAndLanguage(book, en)
                .orElseThrow(() -> new NoSuchElementException("There is no book translate"));
        bookTranslateUa.setId(oldUa.getId());
        bookTranslateEn.setId(oldEn.getId());
        bookTranslateService.updateBookTranslate(bookTranslateUa);
        bookTranslateService.updateBookTranslate(bookTranslateEn);
        logger.info(String.format("Admin | Edit book: a book with such parameters successfully edited: titleUa='%s', " +
                "titleEn='%s', authorsUa='%s', authorsEn='%s'", titleUa, titleEn, authorsStringUa, authorsStringEn));
        return "redirect:/admin/editBook?id=" + id + "&successEditing=true";
    }

    @GetMapping(value = "/blockUser")
    public String blockUser(@RequestParam long id) {
        User user = userService.findById(id).orElseThrow(() -> new NoSuchElementException("There is on such user"));
        user.setBlocked(true);
        userService.update(user);
        logger.info(String.format("Admin | Block user: a user with id='%d' was successfully blocked", id));
        return "redirect:/admin/home?tab=1&page=1";
    }

    @GetMapping(value = "/unblockUser")
    public String unblockUser(@RequestParam long id) {
        User user = userService.findById(id).orElseThrow(() -> new NoSuchElementException("There is on such user"));
        user.setBlocked(false);
        userService.update(user);
        logger.info(String.format("Admin | Unblock user: a user with id='%d' was successfully unblocked", id));
        return "redirect:/admin/home?tab=1&page=1";
    }

    @GetMapping(value = "/deleteLibrarian")
    public String deleteLibrarian(@RequestParam long id) {
        userService.deleteById(id);
        logger.info(String.format("Admin | Delete librarian: a librarian with id='%d' was successfully deleted", id));
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
        logger.info("Admin | Redirect to the add librarian page");
        return "/user/admin/librarianForm";
    }

    @PostMapping(value = "addLibrarian")
    public String addLibrarian(@Valid @ModelAttribute("user") UserDto userDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", userDto);
            logger.info("Admin | Add librarian: invalid input data");
            return "/user/admin/librarianForm";
        }
        User user = new User.Builder()
                .login(userDto.getLogin())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .role(Role.LIBRARIAN)
                .isBlocked(false)
                .build();
        if (userService.findByLogin(userDto.getLogin()).isPresent()) {
            model.addAttribute("createError", true);
            logger.info(String.format("Admin | Add librarian: librarian with login='%s' already exists", userDto.getLogin()));
            return "/user/admin/librarianForm";
        }
        userService.singUpUser(user);
        logger.info(String.format("Admin | Add librarian: librarian with login='%s' was successfully created",
                userDto.getLogin()));
        return "redirect:/admin/addLibrarian?successCreation=true";
    }
}
