package ua.training.controller;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Locale;

@Controller
public class HomeController {

    @GetMapping(value = "/")
    public String getHomePage() {
        Locale locale = LocaleContextHolder.getLocale();
        System.out.println(locale);
        return "index";
    }
}
