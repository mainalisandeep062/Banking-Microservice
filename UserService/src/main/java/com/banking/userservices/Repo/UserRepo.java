package com.banking.userservices.Repo;

import com.banking.userservices.Models.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.lastLogin = CURRENT_TIMESTAMP WHERE u.userId = :id")
    int updateUserLastLogin(@Param("id") Long id);

    Boolean existsByUserId(Long userId);

    Boolean existsByEmail(String email);
}
