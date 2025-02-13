package com.bht.saigonparking.service.contact.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * Customized Error Controller
 * for web output via HTTP port
 *
 * Current Parking-map-service open 2 different ports:
 * 1st one is HTTP port --> communicate server via web
 * 2nd one is gRPC port --> communicate server via RPC
 *
 * @author bht
 */
@Controller
@RequestMapping
public final class CustomizedErrorController implements ErrorController {

    @Override
    public String getErrorPath() {
        return "/error";
    }

    @GetMapping("/error")
    public String handleError() {
        return "error";
    }
}