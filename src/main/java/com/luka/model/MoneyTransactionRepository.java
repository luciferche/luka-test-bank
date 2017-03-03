package com.luka.model;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by luciferche on 3/2/17.
 */
public interface MoneyTransactionRepository  extends CrudRepository<MoneyTransaction, Long> {

//    List<MoneyTransaction> findAllByUser(Long userId);
}
