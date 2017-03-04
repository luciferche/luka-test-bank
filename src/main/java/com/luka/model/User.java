package com.luka.model;


import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * Created by luciferche on 3/1/17.
 */
@Entity
@Table(name = "users")
public class User implements Serializable{

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;


    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "balance")
    private BigDecimal balance;

    public BigDecimal getBalance() {
        if(balance==null) {
            return new BigDecimal(0);
        }
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user")
    private List<MoneyTransaction> transactions;

    public void setTransactions(List<MoneyTransaction> transactions) {
        this.transactions = transactions;
    }

    public Long getId() {
        return id;
    }


    public String getEmail() {
        return email;
    }


    public String getPassword() {
        return password;
    }


    protected User(){}

    public User(User user) {
        this.email = user.email;
        this.password = user.password;
        this.id = user.id;
        if(user.balance==null) {
            user.balance = new BigDecimal(0);
        }
        this.balance = user.balance;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.balance = new BigDecimal(0);
    }


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public List<MoneyTransaction> getTransactions() {
        return transactions;
    }

    public void addTransaction(MoneyTransaction transaction){
        transactions.add(transaction);
    }
}
