package ua.training.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

/**
 * The class that represents an account controller
 */
@Controller
public class AccountController {
    private static final Logger logger = LogManager.getLogger();

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Autowired
    public AccountController(PasswordEncoder passwordEncoder, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    /**
     * The method that returns a registration page
     * @param model - a model
     * @return - a page view
     */
    @GetMapping(value = "/signup")
    public String getSighUpPage(Model model) {
        model.addAttribute("user", new UserDto());
        logger.info("Redirect to the registration page");
        return "signup";
    }

    /**
     * The method that register a new user in the system
     * @param userDto - a user data
     * @param bindingResult - a binding validation result
     * @return - a page view
     */
    @PostMapping(value = "/signup")
    public String sighUp(@Valid @ModelAttribute("user") UserDto userDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.info("Registration: invalid input data");
            return "signup";
        }
        if (userService.findByLogin(userDto.getLogin()).isPresent()) {
            bindingResult.addError(new ObjectError("global", "Login already in use"));
            logger.info(String.format("Registration: login='%s' already in use", userDto.getLogin()));
            return "signup";
        }
        User user = new User.Builder()
                .login(userDto.getLogin())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .role(Role.READER)
                .isBlocked(false)
                .build();
        userService.singUpUser(user);
        logger.info(String.format("Registration: user with login='%s' was successfully registered", userDto.getLogin()));
        return "redirect:/signup?success=true";
    }

    /**
     * The method that returns a login page
     * @return - a page view
     */
    @GetMapping("/login")
    public String showLoginForm() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            logger.info("Redirect to the login page");
            return "login";
        }
        logger.info("Such a user is already logged in");
        return "redirect:/error";
    }

    /**
     * The method that returns a page for a blocked user
     * @return - a page view
     */
    @GetMapping("/user/blocked")
    public String getBlockedPage() {
        logger.info("Redirect to the page of a blocked user");
        return "blocked";
    }
}
