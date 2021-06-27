package ua.training.controller;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.training.model.dto.OrderDto;
import ua.training.model.entity.*;
import ua.training.model.entity.enums.OrderStatus;
import ua.training.model.service.BookService;
import ua.training.model.service.LanguageService;
import ua.training.model.service.OrderService;
import ua.training.model.service.UserService;

import javax.validation.Valid;
import java.util.Locale;

@Controller
@RequestMapping(value = "/reader")
public class ReaderController {

    private final BookService bookService;
    private final LanguageService languageService;
    private final UserService userService;
    private final OrderService orderService;

    public ReaderController(BookService bookService, LanguageService languageService, UserService userService,
                            OrderService orderService) {
        this.bookService = bookService;
        this.languageService = languageService;
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping(value = "/home")
    public String getReaderHomePage(@RequestParam(required = false) boolean successOrder, Model model) {
        if (successOrder) {
            model.addAttribute("successOrder", true);
        } else {
            model.addAttribute("successOrder", false);
        }
        return "/user/reader/home";
    }

    @GetMapping(value = "/orderBook")
    public String getOrderBookPage(@RequestParam long id, Model model) {
        Locale locale = LocaleContextHolder.getLocale();
        Language language = languageService.findByName(locale.getLanguage())
                .orElseThrow(() -> new RuntimeException("There is no such language"));
        BookWithTranslate bookWithTranslate = bookService.findByIdLocated(id, language);
        model.addAttribute("bookWithTranslate", bookWithTranslate);
        model.addAttribute("order", new OrderDto());
        return "/user/reader/orderForm";
    }

    @PostMapping(value = "/orderBook")
    public String orderBook(@Valid @ModelAttribute("order") OrderDto orderDto, @RequestParam long bookId,
                            @RequestParam String userLogin, Model model) {
        Book book = bookService.findById(bookId).orElseThrow(() -> new RuntimeException("There is no such book"));
        if (book.getAmount() <= 0) {
            Locale locale = LocaleContextHolder.getLocale();
            Language language = languageService.findByName(locale.getLanguage())
                    .orElseThrow(() -> new RuntimeException("There is no such language"));
            BookWithTranslate bookWithTranslate = bookService.findByIdLocated(bookId, language);
            model.addAttribute("bookWithTranslate", bookWithTranslate);
            model.addAttribute("order", orderDto);
            model.addAttribute("amountError", true);
            return "/user/reader/orderForm";
        }
        book.setAmount(book.getAmount()-1);
        bookService.updateBook(book);
        User user = userService.findByLogin(userLogin).orElseThrow(() -> new RuntimeException("There is no such user"));
        Order order = new Order.Builder()
                .user(user)
                .book(book)
                .startDate(orderDto.getStartDate())
                .endDate(orderDto.getEndDate())
                .orderStatus(orderDto.getOrderType().equals("subscription")? OrderStatus.RECEIVED: OrderStatus.READER_HOLE)
                .build();
        orderService.addOrder(order);
        return "redirect:/reader/home?successOrder=true";
    }
}
