package com.ipam.api;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipam.api.entity.User;

public interface TUserRepository extends JpaRepository<User, Long> {
    
}
