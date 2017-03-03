package com.luka.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by luciferche on 3/1/17.
 */
public class CustomUserDetails extends User implements UserDetails {

    private static final long serialVersionUID = 1L;
    private List<String> userRoles;


    public CustomUserDetails(User user,List<String> userRoles){
        super(user);
        this.userRoles=userRoles;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        String roles= StringUtils.collectionToCommaDelimitedString(userRoles);
        return AuthorityUtils.commaSeparatedStringToAuthorityList(roles);
    }

    public List<String> getRoles(){
        System.out.println("get first " + this.userRoles.get(0));
        return this.userRoles;
    }

    public boolean hasRole(String role) {
        GrantedAuthority foundAuthority = this.getAuthorities().stream().filter(authority -> authority.equals(role)).findAny().orElse(null);
        return (foundAuthority != null);
    }

    @Override
    public String getUsername() {
        return super.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        // TODO Auto-generated method stub
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }


}