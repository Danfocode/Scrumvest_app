package com.example.Scrumvest.service;

import com.example.Scrumvest.model.entity.Account;
import com.example.Scrumvest.model.repo.AccountRepo;
import com.example.Scrumvest.util.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class AccountService {

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Optional<Account> login(String email, String password) {
        Optional<Account> accountOpt = accountRepo.findByEmailWithRoles(email)
                .filter(account -> passwordEncoder.matches(password, account.getPassword()));
        
        // Si el login es exitoso, añadir la sesión al caché
        accountOpt.ifPresent(account -> Session.addSession(email, account, Session.getCurrentRole()));
        
        return accountOpt;
    }

    @Transactional
    public Account register(Account account) {
        account.setPassword(passwordEncoder.encode(account.getPassword()));

        // Asignar rol por defecto (DEV) si no se especifica
        if (account.getRoles().isEmpty()) {
            account.addRole("DEV");
        }

        return accountRepo.save(account);
    }

    @Transactional(readOnly = true)
    public List<Account> findAllUsers() {
        return accountRepo.findAllWithRoles();
    }

    @Transactional
    public void delete(Long id) {
        accountRepo.findById(id).ifPresent(account -> {
            Session.removeSession(account.getEmail()); // Eliminar sesión del caché
            accountRepo.deleteById(id);
        });
    }

    // Método para verificar roles
    public boolean isCurrentUserInRole(String role) {
        Account currentUser = Session.getCurrentUser();
        return currentUser != null && currentUser.hasRole(role);
    }

    @Transactional(readOnly = true)
    public Optional<Account> findById(Long id) {
        return accountRepo.findById(id);
    }

    @Transactional(readOnly = true)
    public boolean existeCorreo(String email) {
        return accountRepo.findByEmail(email).isPresent();
    }
}