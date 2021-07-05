package ua.training.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ua.training.dto.UserDto;
import ua.training.model.User;
import ua.training.model.enums.Role;
import ua.training.service.UserService;

import javax.validation.Valid;

@Controller
public class AccountController {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Autowired
    public AccountController(PasswordEncoder passwordEncoder, UserService userService) {
        this.passwordEncoder = passwordEncoder;
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
                .password(passwordEncoder.encode(userDto.getPassword()))
                .role(Role.READER)
                .isBlocked(false)
                .build();
        userService.singUpUser(user);
        return "redirect:/signup?success=true";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login";
        }
        return "redirect:/";
    }

    @GetMapping("/user/blocked")
    public String getBlockedPage() {
        return "blocked";
    }
}
