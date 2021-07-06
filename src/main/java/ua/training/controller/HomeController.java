package ua.training.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * The class that represents a controller for home and error pages
 */
@Controller
public class HomeController {
    private static final Logger logger = LogManager.getLogger();

    /**
     * The method that returns a home page
     * @return - a view of a home page
     */
    @GetMapping(value = "/")
    public String getHomePage() {
        logger.info("Redirect to the home page");
        return "index";
    }

    /**
     * The method that returns an error page
     * @return - a view of an error page
     */
    @GetMapping("/error")
    public String getErrorPage() {
        logger.info("Redirect to the error page");
        return "/error/error";
    }
}
