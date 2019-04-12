package com.github.bluecatlee.feign.controller;

import com.github.bluecatlee.feign.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping("/w")
    @ResponseBody
    public WeatherService.Weatcher w(String code) {
        if (code == null || "".equals(code)) {
            code = "101030100";
        }
        return weatherService.w(code);
    }
}
