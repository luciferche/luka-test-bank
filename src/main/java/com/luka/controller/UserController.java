package com.luka.controller;

import com.luka.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by luciferche on 3/2/17.
 */
@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRolesRepository userRolesRepository;

    @Autowired
    private MoneyTransactionRepository moneyTransactionRepository;

    @GetMapping
    String home(HttpServletRequest request, Model model){
        List<User> users = userRepository.findAll();
//        List<Map> userList = userRepository.findAll().stream().map(user -> {
//            Map respMap= new HashMap<String, String>();
//            respMap.put("email", user.getEmail());
//            respMap.put("balance", user.getBalance());
//            respMap.put("balance", user.getBalance());
//            return respMap;
//        }).collect(Collectors.toList());

        model.addAttribute("csrf", getCsrfMap(request));
//        model.addAttribute("users", userList);
        model.addAttribute("users", users);
        model.addAttribute("title", "Users list");
        return "home";
    }

    @PostMapping
    public String createUserView(@RequestParam String email, @RequestParam String password, HttpServletRequest request, Model model){
        System.out.println("usao");
        if(email == null || email.equalsIgnoreCase("") ){
            System.out.println("username not good");
            model.addAttribute("message","username not good");
            return home(request,model);
        }
        if(password ==null || password.equalsIgnoreCase("")) {
            System.out.println("password not good");
            model.addAttribute("message","password not good");
            return home(request,model);
        }
        try {
            User savedUser = userRepository.save(new User(email, password));
            userRolesRepository.save(new UserRole(savedUser.getId(), "USER"));
            System.out.println("saved user -- " + savedUser.toString());
            return home(request, model);
        } catch(DataIntegrityViolationException e) {
            System.out.println("User exists");
            model.addAttribute("message","user exists");
            return home(request,model);
        }

    }

    @GetMapping("/{id}")
    public String getUserView(@PathVariable("id") Long id, HttpServletRequest request, Model model){
        User user = userRepository.findOne(id);

        model.addAttribute("csrf", getCsrfMap(request));

        if(user == null) {
            return  "notfound";
        }
        model.addAttribute("user", user);
        return "user";
    }

    @PostMapping("/{id}/deposit")
    public String depositFundsView(@PathVariable("id") Long id, @RequestParam String amount, HttpServletRequest request, HttpServletResponse response, Model model, Authentication authentication) throws IOException {
        try {
//            UserRole role = userRolesRepository.findByRole
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
//            List<String> rolez= userDetails.getRoles();
            boolean b1= userDetails.hasRole("ADMIN");
            boolean bv = request.isUserInRole("ADMIN");
            if(!userDetails.getId().equals(id)) {
                return "denied";
            }
            BigDecimal bd = new BigDecimal(amount);
            User user = userRepository.findOne(id);
            if(user == null) {
                return "notfound";
            }
            MoneyTransaction tran = moneyTransactionRepository.save(new MoneyTransaction(user, bd));

            user.setBalance(user.getBalance().add(bd));
            userRepository.save(user);
            return getUserView(user.getId(),request, model);
        } catch(NumberFormatException e) {
            System.out.println("invalid amount");
            model.addAttribute("message", "invalid amount");
            return "exception";
        }
    }

    @PostMapping("/{id}/withdraw")
    public String withdrawFundsView(@PathVariable("id") Long id, @RequestParam String amount, HttpServletRequest request, HttpServletResponse response, Model model,Authentication authentication) throws IOException {
        try {

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            List<String> rolez= userDetails.getRoles();

            if(!rolez.contains("ADMIN") || !userDetails.getId().equals(id)) {
                return "denied";
            }
            BigDecimal bd = new BigDecimal(amount);
            User user = userRepository.findOne(id);
            if(user == null) {
                return "notfound";
            }
            MoneyTransaction tran = moneyTransactionRepository.save(new MoneyTransaction(user, bd.negate()));

            user.setBalance(user.getBalance().subtract(bd));
            userRepository.save(user);
            return getUserView(user.getId(),request, model);

        } catch(NumberFormatException e) {
            System.out.println("invalid amount");
            model.addAttribute("message", "invalid amount");
            return "exception";
        }
    }


    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public String handleException(Exception e) {
        return "return error object instead - " + e.getMessage();
    }

    private Map getCsrfMap(HttpServletRequest request) {

        CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
        Map csrfMap = new HashMap<String, String>();
        csrfMap.put("token", token.getToken());
        csrfMap.put("parameterName", token.getParameterName());
        return csrfMap;
    }


}
