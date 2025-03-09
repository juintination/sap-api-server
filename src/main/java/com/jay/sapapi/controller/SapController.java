package com.jay.sapapi.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
@Log4j2
public class SapController {

    @GetMapping("/")
    public String index() {
        return "redirect:/docs";
    }

    @GetMapping("/docs")
    public void redirectToAnotherLink(HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.sendRedirect("http://docs.google.com/spreadsheets/d/1XtaprZiO0qpKhq4kh3CNOoHPTgc19yCJOXGnPUA-X58/edit?usp=drive_web&ouid=102087075933855617997");
    }

}

