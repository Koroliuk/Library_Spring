package ua.training.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private static final Logger logger = LogManager.getLogger();

    @GetMapping(value = "/")
    public String getHomePage() {
        logger.info("Redirect to the home page");
        return "index";
    }

    @GetMapping("/error")
    public String getErrorPage() {
        logger.info("Redirect to the error page");
        return "/error/error";
    }
}
