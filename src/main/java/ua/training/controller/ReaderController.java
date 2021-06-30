package ua.training.controller;

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
import java.util.List;

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
        return "/user/reader/home";
    }

    @GetMapping(value = "/orderBook")
    public String getOrderBookPage(@RequestParam long id, Model model) {
        Language currLanguage = languageService.getCurrentLanguage();
        BookWithTranslate bookWithTranslate = bookService.findByIdLocated(id, currLanguage);
        model.addAttribute("bookWithTranslate", bookWithTranslate)
                .addAttribute("order", new OrderDto());
        return "/user/reader/orderForm";
    }

    @PostMapping(value = "/orderBook")
    public String orderBook(@Valid @ModelAttribute("order") OrderDto orderDto, @RequestParam long bookId,
                            @RequestParam String userLogin, Model model) {
        Language currLanguage = languageService.getCurrentLanguage();
        Book book = bookService.findById(bookId).orElseThrow(() -> new RuntimeException("There is no such book"));
        if (book.getAmount() <= 0) {
            BookWithTranslate bookWithTranslate = bookService.findByIdLocated(bookId, currLanguage);
            model.addAttribute("bookWithTranslate", bookWithTranslate)
                    .addAttribute("order", orderDto)
                    .addAttribute("amountError", true);
            return "/user/reader/orderForm";
        }
        book.setAmount(book.getAmount() - 1);
        bookService.updateBook(book);

        User user = userService.findByLogin(userLogin).orElseThrow(() -> new RuntimeException("There is no such user"));
        Order order = new Order.Builder()
                .user(user)
                .book(book)
                .startDate(orderDto.getStartDate())
                .endDate(orderDto.getEndDate())
                .orderStatus(orderDto.getOrderType().equals("subscription") ? OrderStatus.RECEIVED : OrderStatus.READER_HOLE)
                .build();
        orderService.addOrder(order);
        return "redirect:/reader/home?tab=1&page=1&successOrder=true";
    }

    @GetMapping(value = "/deleteOrder")
    public String deleteOrder(@RequestParam int orderId) {
        orderService.deleteById(orderId);
        // TODO: make differentiation of the result by the input tab parameter
        return "redirect:/reader/home?tab=1&page=1";
    }
}
