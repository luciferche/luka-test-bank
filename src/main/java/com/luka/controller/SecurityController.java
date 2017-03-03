package com.luka.controller;

import com.luka.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by luciferche on 3/1/17.
 */
@Controller
public class SecurityController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String index(){
        return "index";
    }

    ///login please try
    @RequestMapping(value="/login")
    public String showLogin(HttpServletRequest request, Model model){
        System.out.println("loooogin");
        CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
        Map csrfMap = new HashMap<String, String>();
        csrfMap.put("token", token.getToken());
        csrfMap.put("parameterName", token.getParameterName());
        model.addAttribute("csrf", csrfMap);
        return "login";
    }


    @RequestMapping(value="/denied")
    public String denied(){
        return "denied";
    }

    @RequestMapping(value="/exception")
    public String error(){
        return "error";
    }
}
