package com.luka.model;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by luciferche on 3/1/17.
 */
@Repository
public interface UserRolesRepository extends CrudRepository<UserRole, Long> {
    List<String> findRolesByUserid(Long userId);

}