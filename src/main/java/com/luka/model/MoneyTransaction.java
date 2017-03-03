package com.luka.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.DateTimeException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by luciferche on 3/1/17.
 */
@Entity
@Table(name = "transactions")
public class MoneyTransaction implements Serializable {
    @Id //signifies the primary key
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private BigDecimal amount;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date transactionDate;

    public BigDecimal getAmount() {
        return amount;
    }

    public Date getTransactionDate() {

        return transactionDate;
    }
    //    @Column(name = "user_id")
//    private long userId;


    @JsonIgnore
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    protected MoneyTransaction(){

    }

    public MoneyTransaction(User user, BigDecimal amount) {
        this.user = user;
        this.transactionDate = new Date();
        this.amount = amount;
    }
//    public MoneyTransaction(MoneyTransaction moneyTransaction) {
//        this.id = moneyTransaction.id;
//        this.user = moneyTransaction.getUser();
//        this.transactionDate = new Date();
//        this.amount = moneyTransaction.amount;
//    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

//    public long getUserId() {
//        return userId;
//    }
//
//    public void setUserId(long userId) {
//        this.userId = userId;
//    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
