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

    /**
     * [GET : /users]
     * Show all users
     * if user doesn't have ADMIN roles, he can't view list of all users but
     * it's redirected to his own page
     *
     * @param request
     * @param authentication
     * @param model
     * @return
     */
    @GetMapping
    String home(HttpServletRequest request, Authentication authentication, Model model){

        boolean isAdmin = request.isUserInRole("ADMIN");
        if(!isAdmin ) {
            CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
            return getUserView(userDetails.getId(), request, model);

        }
        List<User> users = userRepository.findAll();
        addCsrfToModel(request, model);
        model.addAttribute("users", users);
        model.addAttribute("title", "Users list");
        return "home";
    }

    /**
     * [POST : /users]
     * mapping method for creating users
     *
     * @param email
     * @param password
     * @param request
     * @param authentication
     * @param model
     * @return
     */
    @PostMapping
    public String createUserView(@RequestParam String email, @RequestParam String password, HttpServletRequest request, Authentication authentication, Model model){
        System.out.println("usao");
        if(email == null || email.equalsIgnoreCase("") ){
            System.out.println("username not good");
            model.addAttribute("message","username not good");
            return home(request, authentication, model);
        }
        if(password ==null || password.equalsIgnoreCase("")) {
            System.out.println("password not good");
            model.addAttribute("message","password not good");
            return home(request, authentication, model);
        }
        try {
            User savedUser = userRepository.save(new User(email, password));
            userRolesRepository.save(new UserRole(savedUser.getId(), "ROLE_USER"));
            System.out.println("saved user -- " + savedUser.toString());
            return home(request, authentication, model);
        } catch(DataIntegrityViolationException e) {
            System.out.println("User exists");
            model.addAttribute("message","user exists");
            return home(request, authentication, model);
        }

    }

    /**
     * [GET - /users/{id}]
     * @param id
     * @param request
     * @param model
     * @return
     */
    @GetMapping("/{id}")
    public String getUserView(@PathVariable("id") Long id, HttpServletRequest request, Model model){
        User user = userRepository.findOne(id);;

        boolean isAdmin = request.isUserInRole("ADMIN");
        addCsrfToModel(request, model);
        model.addAttribute("isAdmin", isAdmin);
        if(user == null) {
            return  "notfound";
        }
        model.addAttribute("user", user);
        return "user";
    }

    /**
     * [post - /users/{id}/deposit]
     *  Saves new MoneyTransaction with positive value
     *  Adds amount to user's balance
     *
     * @param id
     * @param amount
     * @param request
     * @param response
     * @param model
     * @param authentication
     * @return
     * @throws IOException
     */
    @PostMapping("/{id}/deposit")
    public String depositFundsView(@PathVariable("id") Long id,
                                   @RequestParam String amount,
                                   HttpServletRequest request,
                                   HttpServletResponse response,
                                   Model model,
                                   Authentication authentication) throws IOException {
        try {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            boolean isAdmin = request.isUserInRole("ADMIN");
            if(!isAdmin && !userDetails.getId().equals(id)) {
                addCsrfToModel(request, model);
                return "denied";
            }
            BigDecimal bd = new BigDecimal(amount);
            User user = userRepository.findOne(id);
            if(user == null) {
                addCsrfToModel(request, model);
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

    /**
     * [GET - /users/{id}/withdraw]
     *
     *  Saves new MoneyTransaction with value negated
     *  Removes amount from user balance if there is enough money, otherwise, fail
     *
     * @param id
     * @param amount
     * @param request
     * @param response
     * @param model
     * @param authentication
     * @return
     * @throws IOException
     */
    @PostMapping("/{id}/withdraw")
    public String withdrawFundsView(@PathVariable("id") Long id, @RequestParam String amount, HttpServletRequest request, HttpServletResponse response, Model model,Authentication authentication) throws IOException {
        try {

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            boolean isAdmin = request.isUserInRole("ADMIN");
            if(!isAdmin && !userDetails.getId().equals(id)) {
                addCsrfToModel(request, model);
                return "denied";
            }
            BigDecimal bd = new BigDecimal(amount);
            User user = userRepository.findOne(id);
            if(user == null) {
                addCsrfToModel(request, model);
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

    private void addCsrfToModel(HttpServletRequest request, Model model) {

        CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
        Map csrfMap = new HashMap<String, String>();
        csrfMap.put("token", token.getToken());
        csrfMap.put("parameterName", token.getParameterName());
        model.addAttribute("csrf", csrfMap);
    }


}
