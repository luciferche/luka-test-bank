package com.luka.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by luciferche on 3/1/17.
 */

public interface UserRepository extends CrudRepository<User, Long> {

    public List<User> findByEmail(String email);
    public List<User> findAll();
}
