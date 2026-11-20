package com.enterprise.featureflag.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PlaygroundController {

    @GetMapping("/playground")
    public String showPlayground(@RequestParam(required = false, defaultValue = "") String apiKey,
                                 Model model) {
        model.addAttribute("apiKey", apiKey);
        return "playground";
    }
}