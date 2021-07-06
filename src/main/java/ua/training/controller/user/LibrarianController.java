package ua.training.controller.user;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.training.model.Language;
import ua.training.model.Order;
import ua.training.model.User;
import ua.training.model.enums.OrderStatus;
import ua.training.model.enums.Role;
import ua.training.service.LanguageService;
import ua.training.service.OrderService;
import ua.training.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * The class that represents a librarian controller
 */
@Controller
@RequestMapping(value = "/librarian")
public class LibrarianController {
    private static final Logger logger = LogManager.getLogger();

    private final UserService userService;
    private final OrderService orderService;
    private final LanguageService languageService;

    public LibrarianController(UserService userService, OrderService orderService, LanguageService languageService) {
        this.userService = userService;
        this.orderService = orderService;
        this.languageService = languageService;
    }

    /**
     * The method that returns a librarian home page
     * @param tab - a tab number
     * @param page - a page number
     * @param model - a model
     * @return - a page view
     */
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
            logger.info("Request the librarian home page with incorrect parameters");
            return "redirect:/error";
        }
        model.addAttribute("tab", tab)
                .addAttribute("currPage", page)
                .addAttribute("amountOfOrders", amountOfOrders)
                .addAttribute("amountOfUsers", amountOfUsers)
                .addAttribute("orders", receivedOrders)
                .addAttribute("readers", users);
        logger.info("Redirect to the librarian home page");
        return "/user/librarian/home";
    }

    /**
     * The method that approves an order
     * @param id - order id
     * @return - a page view
     */
    @GetMapping(value = "/approveOrder")
    public String approveOrder(@RequestParam long id) {
        User user = userService.getCurrentUser();
        orderService.approveOrder(id);
        logger.info(String.format("Librarian | Approve order: librarian %s successfully approve order with id='%d'",
                user.getLogin(), id));
        return "redirect:/librarian/home?tab=1&page=1";
    }

    /**
     * The method that cancels an order
     * @param id - order id
     * @return - a page view
     */
    @GetMapping(value = "/cancelOrder")
    public String cancelOrder(@RequestParam long id) {
        User user = userService.getCurrentUser();
        orderService.cancelOrder(id);
        logger.info(String.format("Librarian | Cancel order: librarian %s successfully cancel order with id='%d'",
                user.getLogin(), id));
        return "redirect:/librarian/home?tab=1&page=1";
    }

    /**
     * The method that returns a subscription page of a specified user
     * @param userId - a user id
     * @param page - a page number
     * @param model - a model
     * @return - a page view
     */
    @GetMapping(value = "/getReaderBooks")
    public String getUsersBook(@RequestParam long userId, @RequestParam int page, Model model) {
        User currentUser = userService.getCurrentUser();
        int amountOfBookOnPage = 5;
        Language currLanguage = languageService.getCurrentLanguage();
        User user = userService.findById(userId).orElseThrow(() -> new NoSuchElementException("There is no such user"));
        int totalAmount = orderService.getAmountByUserAnd2OrderStatus(user, OrderStatus.APPROVED, OrderStatus.OVERDUE);
        int amount = (totalAmount - 1) / amountOfBookOnPage + 1;
        List<Order> orders = orderService.findAllByUserAnd2OrderStatus(user, OrderStatus.APPROVED, OrderStatus.OVERDUE,
                page - 1, amountOfBookOnPage, currLanguage);
        model.addAttribute("orders", orders)
                .addAttribute("amount", amount)
                .addAttribute("readerId", userId)
                .addAttribute("currPage", page);
        logger.info(String.format("Librarian | Get reader's books: librarian %s request the books of reader with id='%s'",
                currentUser.getLogin(), userId));
        return "/user/librarian/readerBook";
    }
}
