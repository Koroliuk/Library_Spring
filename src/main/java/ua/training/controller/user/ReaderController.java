package ua.training.controller.user;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import ua.training.dto.OrderDto;
import ua.training.model.*;
import ua.training.model.enums.OrderStatus;
import ua.training.service.BookService;
import ua.training.service.LanguageService;
import ua.training.service.OrderService;
import ua.training.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * The class that represents a reader controller
 */
@Controller
@RequestMapping(value = "/reader")
public class ReaderController {
    private static final Logger logger = LogManager.getLogger();

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

    /**
     * The method that returns a reader home page
     * @param tab - a tab number
     * @param page - a page number
     * @param successOrder - a not required parameter that indicates success of a previous order
     * @param model - a model
     * @return - a page view
     */
    @GetMapping(value = "/home")
    public String getReaderHomePage(@RequestParam int tab, @RequestParam int page,
                                    @RequestParam(required = false) boolean successOrder, Model model) {
        User currentUser = userService.getCurrentUser();
        Language currentLanguage = languageService.getCurrentLanguage();

        orderService.checkUserOrders(currentUser);

        int amountOrdersOnPage = 5;
        int totalAmount1 = orderService.getAmountByUserAnd2OrderStatus(currentUser, OrderStatus.APPROVED, OrderStatus.OVERDUE);
        int totalAmount2 = orderService.getAmountByUserAndOrderStatus(currentUser, OrderStatus.READER_HOLE);
        int totalAmount3 = orderService.getAmountByUserAnd2OrderStatus(currentUser, OrderStatus.CANCELED, OrderStatus.RECEIVED);
        int amount1 = (totalAmount1 - 1) / amountOrdersOnPage + 1;
        int amount2 = (totalAmount2 - 1) / amountOrdersOnPage + 1;
        int amount3 = (totalAmount3 - 1) / amountOrdersOnPage + 1;

        List<Order> approvedAndOverdueOrders;
        List<Order> readingHoleOrders;
        List<Order> canceledAndReceivedOrders;
        if (tab == 1) {
            approvedAndOverdueOrders = orderService.findAllByUserAnd2OrderStatus(currentUser, OrderStatus.APPROVED,
                    OrderStatus.OVERDUE, page - 1, amountOrdersOnPage, currentLanguage);
            readingHoleOrders = orderService.findAllByUserAndOrderStatus(currentUser, OrderStatus.READER_HOLE, 0,
                    amountOrdersOnPage, currentLanguage);
            canceledAndReceivedOrders = orderService.findAllByUserAnd2OrderStatus(currentUser, OrderStatus.CANCELED,
                    OrderStatus.RECEIVED, 0, amountOrdersOnPage, currentLanguage);

        } else if (tab == 2) {
            approvedAndOverdueOrders = orderService.findAllByUserAnd2OrderStatus(currentUser, OrderStatus.APPROVED,
                    OrderStatus.OVERDUE, 0, amountOrdersOnPage, currentLanguage);
            readingHoleOrders = orderService.findAllByUserAndOrderStatus(currentUser, OrderStatus.READER_HOLE, page - 1,
                    amountOrdersOnPage, currentLanguage);
            canceledAndReceivedOrders = orderService.findAllByUserAnd2OrderStatus(currentUser, OrderStatus.CANCELED,
                    OrderStatus.RECEIVED, 0, amountOrdersOnPage, currentLanguage);
        } else if (tab == 3) {
            approvedAndOverdueOrders = orderService.findAllByUserAnd2OrderStatus(currentUser, OrderStatus.APPROVED,
                    OrderStatus.OVERDUE, 0, amountOrdersOnPage, currentLanguage);
            readingHoleOrders = orderService.findAllByUserAndOrderStatus(currentUser, OrderStatus.READER_HOLE, 0,
                    amountOrdersOnPage, currentLanguage);
            canceledAndReceivedOrders = orderService.findAllByUserAnd2OrderStatus(currentUser, OrderStatus.CANCELED,
                    OrderStatus.RECEIVED, page - 1, amountOrdersOnPage, currentLanguage);
        } else {
            logger.info("Request the reader home page with incorrect parameters");
            return "redirect:/error";
        }
        if (successOrder) {
            model.addAttribute("successOrder", true);
        } else {
            model.addAttribute("successOrder", false);
        }
        model.addAttribute("tab", tab)
                .addAttribute("currPage", page)
                .addAttribute("amount1", amount1)
                .addAttribute("amount2", amount2)
                .addAttribute("amount3", amount3)
                .addAttribute("orders1", approvedAndOverdueOrders)
                .addAttribute("orders2", readingHoleOrders)
                .addAttribute("orders3", canceledAndReceivedOrders);
        logger.info("Redirect to the reader home page");
        return "/user/reader/home";
    }

    /**
     * The method that returns an order book page
     * @param id - an order id
     * @param model - a model
     * @return - a page view
     */
    @GetMapping(value = "/orderBook")
    public String getOrderBookPage(@RequestParam long id, Model model) {
        Language currLanguage = languageService.getCurrentLanguage();
        BookWithTranslate bookWithTranslate = bookService.findByIdLocated(id, currLanguage);
        model.addAttribute("bookWithTranslate", bookWithTranslate)
                .addAttribute("order", new OrderDto());
        logger.info("Redirect to the make-order page");
        return "/user/reader/orderForm";
    }

    /**
     * The method that processes a book order
     * @param orderDto - an order data
     * @param bindingResult - a binding validation result
     * @param bookId - a book id
     * @param userLogin - a user login
     * @param model - a model
     * @return - a page view
     */
    @PostMapping(value = "/orderBook")
    public String orderBook(@Valid @ModelAttribute("order") OrderDto orderDto, BindingResult bindingResult,
                            @RequestParam long bookId, @RequestParam String userLogin, Model model) {
        Language currLanguage = languageService.getCurrentLanguage();
        if (bindingResult.hasErrors()) {
            BookWithTranslate bookWithTranslate = bookService.findByIdLocated(bookId, currLanguage);
            model.addAttribute("bookWithTranslate", bookWithTranslate)
                    .addAttribute("order", orderDto);
            logger.info("Reader | Order book: invalid input data");
            return "/user/reader/orderForm";
        }
        if (orderDto.getEndDate().isBefore(orderDto.getStartDate())) {
            bindingResult.addError(new ObjectError("global", "Date of start must be earlier than end date"));
            BookWithTranslate bookWithTranslate = bookService.findByIdLocated(bookId, currLanguage);
            model.addAttribute("bookWithTranslate", bookWithTranslate)
                    .addAttribute("order", orderDto);
            logger.info("Reader | Order book: invalid input data");
            return "/user/reader/orderForm";
        }
        Book book = bookService.findById(bookId).orElseThrow(() -> new NoSuchElementException("There is no such book"));
        if (book.getAmount() <= 0) {
            BookWithTranslate bookWithTranslate = bookService.findByIdLocated(bookId, currLanguage);
            model.addAttribute("bookWithTranslate", bookWithTranslate)
                    .addAttribute("order", orderDto)
                    .addAttribute("amountError", true);
            logger.info("Reader | Order book: there are no available copies of the book");
            return "/user/reader/orderForm";
        }
        book.setAmount(book.getAmount() - 1);
        bookService.updateBook(book);

        User user = userService.findByLogin(userLogin).orElseThrow(() -> new NoSuchElementException("There is no such user"));
        Order order = new Order.Builder()
                .user(user)
                .book(book)
                .startDate(orderDto.getStartDate())
                .endDate(orderDto.getEndDate())
                .orderStatus(orderDto.getOrderType().equals("subscription") ? OrderStatus.RECEIVED : OrderStatus.READER_HOLE)
                .build();
        orderService.addOrder(order);
        logger.info(String.format("Reader | Order book: reader %s successfully order book with id='%d'", userLogin, bookId));
        return "redirect:/reader/home?tab=1&page=1&successOrder=true";
    }

    /**
     * The method that deletes an order
     * @param orderId - an order id
     * @param tab - a tab number that indicates an order group
     * @return - a page view
     */
    @GetMapping(value = "/deleteOrder")
    public String deleteOrder(@RequestParam int orderId, @RequestParam int tab) {
        User user = userService.getCurrentUser();
        orderService.deleteById(orderId);
        if (tab > 0 && tab <= 3) {
            logger.info(String.format("Reader | Delete order: reader %s successfully delete order with id='%d'",
                    user.getLogin(), orderId));
            return "redirect:/reader/home?tab=" + tab + "&page=1";
        } else {
            logger.info(String.format("Reader | Delete order: an error occurred when user %s was deleting order with id='%d'",
                    user.getLogin(), orderId));
            return "error/error";
        }
    }
}
