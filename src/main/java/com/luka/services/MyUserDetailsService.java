package com.luka.services;

import com.luka.model.CustomUserDetails;
import com.luka.model.User;
import com.luka.model.UserRepository;
import com.luka.model.UserRolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luciferche on 3/1/17.
 */

@Service("MyUserDetailsService")
public class MyUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserRolesRepository userRolesRepository;

    @Autowired
    public MyUserDetailsService(UserRepository userRepository,UserRolesRepository userRolesRepository) {
        this.userRepository = userRepository;
        this.userRolesRepository=userRolesRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<User> users = userRepository.findByEmail(username);
        if(users == null || users.size() == 0){
            throw new UsernameNotFoundException("No user with email: "+username);
        }else{

            List<String> userRoles= userRolesRepository.findRolesByUserid(users.get(0).getId());
            if(userRoles.size()>0) {
                return new CustomUserDetails(users.get(0),userRoles);
            } else {
                throw new UsernameNotFoundException("No user with email: "+username);
//                throw new BadCredentialsException("Ne postoji takav user");
            }
        }
    }
/*
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<User> users = userRepository.findByEmail(username);
        if(users == null || users.size() == 0){
            throw new UsernameNotFoundException("No user with email: "+username);
        }else{
            List<String> userRoles=userRolesRepository.findRolesByEmail(username);
            if(userRoles.size()>0) {
                return new CustomUserDetails(users.get(0),userRoles);
            } else {
                throw new UsernameNotFoundException("No user with email: "+username);
//                throw new BadCredentialsException("Ne postoji takav user");
            }
        }
    }*/
//
//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        auth
//                .inMemoryAuthentication()
//                .withUser("user").password("password").roles("USER");
//    }
}
