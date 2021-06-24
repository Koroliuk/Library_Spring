package ua.training.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ua.training.model.dto.UserDto;
import ua.training.model.entity.User;
import ua.training.model.entity.enums.Role;
import ua.training.model.service.UserService;

import javax.validation.Valid;

@Controller
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/signup")
    public String getSighUpPage(Model model) {
        model.addAttribute("user", new UserDto());
        return "signup";
    }

    @PostMapping(value = "/signup")
    public String sighUp(@Valid @ModelAttribute("user") UserDto userDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup";
        }
        if (userService.findByLogin(userDto.getLogin()).isPresent()) {
            bindingResult.addError(new ObjectError("global", "Login already in use"));
            return "signup";
        }
        User user = new User.Builder()
                .login(userDto.getLogin())
                .password(userDto.getPassword())
                .role(Role.READER)
                .isBlocked(false)
                .build();
        userService.singUpUser(user);
        return "redirect:/signup?success=true";
    }
}
