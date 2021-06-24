package ua.training.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Autowired
    public UserController(PasswordEncoder passwordEncoder, UserService userService) {
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

    @GetMapping(value = "/login")
    public String getLoginPage() {
        return "login";
    }

//    @PostMapping(value = "/login")
//    public String login() {
//        SecurityContext securityContext = SecurityContextHolder.getContext();
//        Authentication authentication = securityContext.getAuthentication();
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//        User user = userService.findByLogin(userDetails.getUsername()).orElseThrow(
//                () -> new UsernameNotFoundException("There is no user with such name"));
//        if (user.isBlocked()) {
//            return "forward:blocked";
//        } else {
//            if (user.getRole() == Role.READER) {
//                return "redirect:/reader/home";
//            } else if (user.getRole() == Role.LIBRARIAN) {
//                return "redirect:/librarian/home";
//            } else if (user.getRole() == Role.ADMIN) {
//                return "redirect:/admin/home";
//            } else {
//                return "error/error";
//            }
//        }
//    }
}
