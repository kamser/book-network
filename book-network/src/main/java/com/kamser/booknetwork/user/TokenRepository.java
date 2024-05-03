package com.kamser.booknetwork.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {
    Optional<Token> findByToken(String token);  // I have to use the Optional when there is case where null pointer exception or NPE are posible, this helps to avoid those issues.
}
