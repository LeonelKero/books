package com.workbeattalent.books.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping
    public String home() {
        return "pages/home";
    }

    @GetMapping(path = {"/books"})
    public String getBooks() {
        return "pages/books";
    }

    @GetMapping(path = {"/contact"})
    public String getIntouch() {
        return "pages/contact";
    }

    @GetMapping(path = {"/swagger"})
    public String docs() {
        return "redirect:/swagger-ui.html";
    }

}
