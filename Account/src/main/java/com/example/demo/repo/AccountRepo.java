package com.example.demo.repo;
import com.example.demo.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepo extends JpaRepository<Account, Integer> {
    Optional<Account> findByAccountId(Integer integer);
}
