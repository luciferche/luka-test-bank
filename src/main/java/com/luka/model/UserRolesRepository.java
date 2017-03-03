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
//
//    @Query("select a.role from UserRole a, User b where b.email=?1 and a.id=b.id")
//    public List<String> findRolesByEmail(String email);
    List<String> findRolesByUserid(Long userId);

}