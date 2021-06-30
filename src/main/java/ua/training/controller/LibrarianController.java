package ua.training.controller;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.training.model.entity.Language;
import ua.training.model.entity.Order;
import ua.training.model.entity.User;
import ua.training.model.entity.enums.OrderStatus;
import ua.training.model.entity.enums.Role;
import ua.training.model.service.LanguageService;
import ua.training.model.service.OrderService;
import ua.training.model.service.UserService;

import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping(value = "/librarian")
public class LibrarianController {

    private final UserService userService;
    private final OrderService orderService;
    private final LanguageService languageService;

    public LibrarianController(UserService userService, OrderService orderService, LanguageService languageService) {
        this.userService = userService;
        this.orderService = orderService;
        this.languageService = languageService;
    }

    @GetMapping(value = "/home")
    public String getLibrarianHomePage(@RequestParam int tab, @RequestParam int page, Model model) {
        Locale locale = LocaleContextHolder.getLocale();
        Language language = languageService.findByName(locale.getLanguage())
                .orElseThrow(() -> new RuntimeException("There is no such language"));
        int amount1 = orderService.getAmountByOrderStatus(OrderStatus.RECEIVED);
        int amount2 = userService.getAmountByRole(Role.READER);
        System.out.println(amount1);
        System.out.println(amount2);
        if (tab == 1) {
            List<Order> receivedOrders = orderService.getReceivedOrders(OrderStatus.RECEIVED, page-1, 5,
                    language);
            List<User> users = userService.findAllByRole(Role.READER, 0, 5);
            model.addAttribute("orders", receivedOrders);
            model.addAttribute("readers", users);
        } else if (tab == 2) {
            List<Order> receivedOrders = orderService.getReceivedOrders(OrderStatus.RECEIVED, 0, 5,
                    language);
            List<User> users = userService.findAllByRole(Role.READER, page-1, 5);
            model.addAttribute("orders", receivedOrders);
            model.addAttribute("readers", users);
        } else {
            return "redirect:/error";
        }
        model.addAttribute("tab", tab);
        model.addAttribute("amount1", (amount1-1)/5+1);
        model.addAttribute("amount2", (amount2-1)/5+1);
        return "/user/librarian/home";
    }

    @GetMapping(value = "/approveOrder")
    public String approveOrder(@RequestParam long id) {
        orderService.approveOrder(id);
        return "redirect:/librarian/home?tab=1&page=1";
    }

    @GetMapping(value = "/cancelOrder")
    public String cancelOrder(@RequestParam long id) {
        orderService.cancelOrder(id);
        return "redirect:/librarian/home?tab=1&page=1";
    }

    @GetMapping(value = "/getReaderBooks")
    public String getUsersBook(@RequestParam long userId, @RequestParam int page, Model model) {
        Locale locale = LocaleContextHolder.getLocale();
        Language language = languageService.findByName(locale.getLanguage())
                .orElseThrow(() -> new RuntimeException("There is no such language"));
        User user = userService.findById(userId).orElseThrow(() -> new RuntimeException("There is no such user"));
        List<Order> orders = orderService.getApprovedAndOverdueOrdersByUserId(user, OrderStatus.APPROVED, OrderStatus.OVERDUE,
                page-1, 5, language);
        model.addAttribute("orders", orders);
        int amount = orderService.getAmountByUserAnd2OrOrderStatus(user, OrderStatus.APPROVED, OrderStatus.OVERDUE);
        model.addAttribute("amount", (amount-1)/5+1);
        model.addAttribute("readerId", userId);
        model.addAttribute("currPage", page);
        return "/user/librarian/readerBook";
    }
}
