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
    @Query(
            value ="""
                        select u.*
                        from `user` u
                        where u.email=:email
                    """, nativeQuery = true)
    Optional<User> findByEmail(@Param("email")String email);

    @Transactional
    @Modifying
    @Query(
            value ="""
                        update `user` u
                        set u.last_login=CURRENT_TIMESTAMP()
                        WHERE u.user_id=:id
                    """, nativeQuery = true)
    int updateUserLastLogin(@Param("id")Long id);

    Boolean existsByUserId(Long userId);

    Boolean existsByEmail(String email);
}
