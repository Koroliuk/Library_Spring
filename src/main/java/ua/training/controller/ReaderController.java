package ua.training.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/reader")
public class ReaderController {

    @GetMapping(value = "/home")
    public String getReaderHomePage() {
        return "/user/reader/home";
    }
}
