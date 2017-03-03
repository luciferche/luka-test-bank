package com.luka.controller;

import com.luka.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.security.Principal;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by luka on 3/1/17.
 * main rest controller for api calls
 *
 *
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRolesRepository userRolesRepository;

    @Autowired
    private MoneyTransactionRepository moneyTransactionRepository;

    @GetMapping("/users")
    public ResponseEntity<Collection<User>> getAllUsers(){
        return ResponseEntity.ok().body((Collection<User>) userRepository.findAll());
    }

    /**
     * Create users from Object passed by POST
     *
     * @param user
     * @param ucBuilder
     * @return ResponseEntity.badRequest() or ResponseEntity.ok()
     */
    @PostMapping("/users")
    public ResponseEntity<Void> create(@RequestBody User user, UriComponentsBuilder ucBuilder){
        System.out.println("usao");
        if(user == null) {
            System.out.println("user nije dobar");
            return  ResponseEntity.badRequest().build();

        }
        if(user.getEmail() == null || user.getEmail().equalsIgnoreCase("") ){
            System.out.println("username nije dobar");
            return  ResponseEntity.badRequest().build();
        }
        if(user.getPassword() ==null || user.getPassword().equalsIgnoreCase("")) {
            System.out.println("sifra nije dobra");
            return  ResponseEntity.badRequest().build();
        }
        try {
            User savedUser = userRepository.save(user);

            userRolesRepository.save(new UserRole(savedUser.getId(), "USER"));
            System.out.println("saved user -- " + savedUser.toString());
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(ucBuilder.path("/users/{id}").buildAndExpand(savedUser.getId()).toUri());
            return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
        } catch(DataIntegrityViolationException e) {
            System.out.println("User exists");
            return  ResponseEntity.badRequest().build();
        }

    }


    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") Long id){
        User user = userRepository.findOne(id);
        if(user == null) {
            return  ResponseEntity.notFound().build();
        }
        Map<String, Object> respMap = new HashMap<>();
        respMap.put("user",user.getEmail());
        respMap.put("balance",user.getBalance());

        return ResponseEntity.ok().body(user);
    }


    @PostMapping("/users/{id}/deposit")
    public ResponseEntity<Void> depositFunds(@PathVariable("id") Long id, @RequestParam String amount, UriComponentsBuilder ucb) {
        try {
            BigDecimal bd = new BigDecimal(amount);
            User user = userRepository.findOne(id);
            if(user == null) {;
                return  ResponseEntity.notFound().build();
            }
            MoneyTransaction tran = moneyTransactionRepository.save(new MoneyTransaction(user, bd));
            if(tran.getId() == null || tran.getId() == 1) {
                System.out.println("Transaction not saved properly");
                return ResponseEntity.badRequest().build();
            } else {
                user.setBalance(user.getBalance().add(bd));
                userRepository.save(user);
            }
            return ResponseEntity.created(ucb.path("/users/{id}").buildAndExpand(user.getId()).toUri()).build();
        } catch(NumberFormatException e) {
            System.out.println("invalid amount");
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/users/{id}/withdraw")
    public ResponseEntity<MoneyTransaction> withdrawFunds(@PathVariable("id") Long id, @RequestParam String amount, UriComponentsBuilder ucb) {
        try {
            BigDecimal bd = new BigDecimal(amount);
            User user = userRepository.findOne(id);
            if(user == null) {;
                return  ResponseEntity.notFound().build();
            }
            if(user.getBalance().compareTo(bd) < 0) {
                System.out.println("not enough funds");
                return ResponseEntity.badRequest().build();
            } else {
                MoneyTransaction tran = moneyTransactionRepository.save(new MoneyTransaction(user, bd.negate()));
                if(tran.getId() == null || tran.getId() == 1) {
                    System.out.println("Transaction not saved properly");
                    return ResponseEntity.badRequest().build();
                } else {
                    user.setBalance(user.getBalance().subtract(bd));
                    userRepository.save(user);
                }
                return ResponseEntity.created(ucb.path("/users/{id}/transactions").buildAndExpand(user.getId()).toUri()).build();
            }

        } catch(NumberFormatException e) {
            System.out.println("invalid amount");
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("/transactions/{transactionId}")
    public ResponseEntity<Map> getOneTransaction(@PathVariable("transactionID") String transactionId){
        Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");

        MoneyTransaction transaction = moneyTransactionRepository.findOne(Long.parseLong(transactionId));
        HashMap<String, Object> tranMap = new HashMap<>();
        tranMap.put("amount", transaction.getAmount());
        tranMap.put("date", format.format(transaction.getTransactionDate()));
        return ResponseEntity.ok().body(tranMap);
    }

}
