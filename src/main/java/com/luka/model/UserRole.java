package com.luka.model;

import javax.persistence.*;

/**
 * Created by luciferche on 3/1/17.
 */

@Entity
@Table(name="user_roles")
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="user_role_id")
    private Long userroleid;

    @Column(name="userid")
    private Long userid;

    @Column(name="role")
    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public Long getUserroleid() {
        return userroleid;
    }

    public void setUserroleid(Long userroleid) {
        this.userroleid = userroleid;
    }

    public UserRole(){}


    public UserRole(Long userid, String role) {
        this.userid = userid;
        this.role = role;
    }

}
