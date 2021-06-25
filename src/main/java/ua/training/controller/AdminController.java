package ua.training.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.training.model.service.UserService;

@Controller
@RequestMapping(value = "/admin")
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/home")
    public String getAdminHomePage(Model model, @RequestParam Integer pageNo) {
        if (pageNo == null) {
            pageNo = 1;
        }
        int amountOfPages = (userService.getAmountOfUsers()-1)/5+1;
        model.addAttribute("amountOfPages", amountOfPages);
        model.addAttribute("users", userService.findPaginated(pageNo-1, 5));
        return "/user/admin/home";
    }
}
