package com.example.tripGo.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    @GetMapping("/admin1")
    public String admin1() {
        return "admin1";
    }

    @GetMapping("/routes")
    public String routes() {
        return "routes";
    }

    @GetMapping("/busticket")
    public String busticket() {
        return "busticket";
    }

    @GetMapping("/bookingdetails")
    public String bookingdetails() {
        return "bookingdetails";
    }

    @GetMapping("/bookingsuccess")
    public String bookingsuccess() {
        return "bookingsuccess";
    }

    @GetMapping("/mybooking")  // Note: hyphen, singular
    public String mybooking() {
        return "mybooking";
    }

    @GetMapping("/adminbooking")
    public String adminbooking() {
        return "adminbooking";
    }

    @GetMapping("/customers")
    public String customers() {
        return "customers";
    }

    @GetMapping("/booking")
    public String booking() {
        return "booking";
    }

    @GetMapping("/schedule")
    public String schedule() {
        return "schedule";
    }

    @GetMapping("/bus")
    public String bus() {
        return "bus";
    }
}
