package com.health.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.health.model.User;

import java.util.Optional;
import java.util.Date;
import java.util.List;

@Repository
public interface UserDAO extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameAndPassword(String username, String password);
  
    
    @Query("SELECT u FROM User u WHERE u.lastLoginDate >= :date")
    List<User> findActiveUsers(@Param("date") Date date);
    
    @Query("SELECT u FROM User u WHERE u.tdee IS NOT NULL AND u.waterTarget IS NOT NULL")
    List<User> findUsersWithCompletedProfile();
}