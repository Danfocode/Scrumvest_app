package com.example.Scrumvest.model.repo;

import com.example.Scrumvest.model.entity.Account;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface AccountRepo extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String email);
    
    @Query("SELECT a FROM Account a LEFT JOIN FETCH a.roles WHERE a.email = :email")
    Optional<Account> findByEmailWithRoles(@Param("email") String email);
    
    @Query("SELECT DISTINCT a FROM Account a LEFT JOIN FETCH a.roles")
    List<Account> findAllWithRoles();
}