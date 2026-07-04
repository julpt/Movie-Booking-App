package org.example.movie_booking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestErrorController {

    @GetMapping("/trigger-500")
    public String triggerServerError() {
        // ynhandled exception
        throw new RuntimeException("This is a deliberate test server error.");
    }
}