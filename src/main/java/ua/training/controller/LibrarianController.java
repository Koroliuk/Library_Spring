package ua.training.controller;

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
        int amountOfOrdersOnPage = 5;
        Language currLanguage = languageService.getCurrentLanguage();
        int totalAmountOfOrders = orderService.getAmountByOrderStatus(OrderStatus.RECEIVED);
        int totalAmountOfUsers = userService.getAmountByRole(Role.READER);
        int amountOfOrders = (totalAmountOfOrders - 1) / amountOfOrdersOnPage + 1;
        int amountOfUsers = (totalAmountOfUsers - 1) / amountOfOrdersOnPage + 1;
        List<Order> receivedOrders;
        List<User> users;
        if (tab == 1) {
            receivedOrders = orderService.findAllByOrderStatus(OrderStatus.RECEIVED, page - 1,
                    amountOfOrdersOnPage, currLanguage);
            users = userService.findAllByRole(Role.READER, 0, amountOfOrdersOnPage);
        } else if (tab == 2) {
            receivedOrders = orderService.findAllByOrderStatus(OrderStatus.RECEIVED, 0,
                    amountOfOrdersOnPage, currLanguage);
            users = userService.findAllByRole(Role.READER, page - 1, amountOfOrdersOnPage);
        } else {
            return "redirect:/error";
        }
        model.addAttribute("tab", tab)
                .addAttribute("currPage", page)
                .addAttribute("amountOfOrders", amountOfOrders)
                .addAttribute("amountOfUsers", amountOfUsers)
                .addAttribute("orders", receivedOrders)
                .addAttribute("readers", users);
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
        int amountOfBookOnPage = 5;
        Language currLanguage = languageService.getCurrentLanguage();
        User user = userService.findById(userId).orElseThrow(() -> new RuntimeException("There is no such user"));
        int totalAmount = orderService.getAmountByUserAnd2OrderStatus(user, OrderStatus.APPROVED, OrderStatus.OVERDUE);
        int amount = (totalAmount - 1) / amountOfBookOnPage + 1;
        List<Order> orders = orderService.findAllByUserAnd2OrderStatus(user, OrderStatus.APPROVED, OrderStatus.OVERDUE,
                page - 1, amountOfBookOnPage, currLanguage);
        model.addAttribute("orders", orders)
                .addAttribute("amount", amount)
                .addAttribute("readerId", userId)
                .addAttribute("currPage", page);
        return "/user/librarian/readerBook";
    }
}
