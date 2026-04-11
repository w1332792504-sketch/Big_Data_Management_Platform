package com.datamanager.repository;

import com.datamanager.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    Page<User> findByUsernameContaining(String username, Pageable pageable);
    Page<User> findByStatus(Integer status, Pageable pageable);
    Page<User> findByUsernameContainingAndStatus(String username, Integer status, Pageable pageable);
}
