package ua.training.controller;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
import ua.training.secutiry.CustomUserDetails;

import javax.validation.Valid;
import java.util.List;
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
    public String getReaderHomePage(@RequestParam int tab, @RequestParam int page, @RequestParam(required = false) boolean successOrder, Model model) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails)principal).getUsername();
        User user = userService.findByLogin(username)
                .orElseThrow(() -> new RuntimeException("There is no such user"));
        Locale locale = LocaleContextHolder.getLocale();
        Language language = languageService.findByName(locale.getLanguage())
                .orElseThrow(() -> new RuntimeException("There is no such language"));
        orderService.checkUserOrders(user);
        int amount1 = orderService.getAmountByUserAnd2OrOrderStatus(user, OrderStatus.APPROVED, OrderStatus.OVERDUE);
        int amount2 = orderService.getAmountByUserAndOrderStatus(user, OrderStatus.READER_HOLE);
        int amount3 = orderService.getAmountByUserAnd2OrOrderStatus(user, OrderStatus.CANCELED, OrderStatus.RECEIVED);
        if (tab == 1) {
            List<Order> approvedAndOverdueOrders = orderService.getApprovedAndOverdueOrdersByUserId(user, OrderStatus.APPROVED,
                    OrderStatus.OVERDUE, page -1, 5, language);
            List<Order> readingHoleOrders = orderService.getReadingHoleOrders(user, OrderStatus.READER_HOLE, 0,
                    5, language);
            List<Order> canceledAndReceivedOrders = orderService.getApprovedAndOverdueOrdersByUserId(user, OrderStatus.CANCELED,
                    OrderStatus.RECEIVED, 0, 5, language);
            model.addAttribute("orders1", approvedAndOverdueOrders);
            model.addAttribute("orders2", readingHoleOrders);
            model.addAttribute("orders3", canceledAndReceivedOrders);
        } else if (tab == 2) {
            List<Order> approvedAndOverdueOrders = orderService.getApprovedAndOverdueOrdersByUserId(user, OrderStatus.APPROVED,
                    OrderStatus.OVERDUE, 0, 5, language);
            List<Order> readingHoleOrders = orderService.getReadingHoleOrders(user, OrderStatus.READER_HOLE, page -1,
                    5, language);
            List<Order> canceledAndReceivedOrders = orderService.getApprovedAndOverdueOrdersByUserId(user, OrderStatus.CANCELED,
                    OrderStatus.RECEIVED, 0, 5, language);
            model.addAttribute("orders1", approvedAndOverdueOrders);
            model.addAttribute("orders2", readingHoleOrders);
            model.addAttribute("orders3", canceledAndReceivedOrders);
        } else if (tab == 3) {
            List<Order> approvedAndOverdueOrders = orderService.getApprovedAndOverdueOrdersByUserId(user, OrderStatus.APPROVED,
                    OrderStatus.OVERDUE, 0, 5, language);
            List<Order> readingHoleOrders = orderService.getReadingHoleOrders(user, OrderStatus.READER_HOLE, 0,
                    5, language);
            List<Order> canceledAndReceivedOrders = orderService.getApprovedAndOverdueOrdersByUserId(user, OrderStatus.CANCELED,
                    OrderStatus.RECEIVED, page -1, 5, language);
            model.addAttribute("orders1", approvedAndOverdueOrders);
            model.addAttribute("orders2", readingHoleOrders);
            model.addAttribute("orders3", canceledAndReceivedOrders);
        } else {
            return "redirect:/error";
        }
        model.addAttribute("amount1", (amount1-1)/5+1);
        model.addAttribute("amount2", (amount2-1)/5+1);
        model.addAttribute("amount3", (amount3-1)/5+1);
        model.addAttribute("tab", tab);
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
        return "redirect:/reader/home?tab=1&page=1&successOrder=true";
    }
}
