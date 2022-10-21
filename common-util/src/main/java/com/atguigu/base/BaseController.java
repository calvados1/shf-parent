package com.atguigu.base;

import org.springframework.ui.Model;

public class BaseController {
    private final static String PAGE_SUCCESS = "common/successPage";

    public String successPage(Model model, String successMessage) {
        model.addAttribute("messagePage", successMessage);
        return PAGE_SUCCESS;
    }
}
