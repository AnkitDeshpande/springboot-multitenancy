package com.springsecurity.springsecurity.repository;

import com.springsecurity.springsecurity.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);
}
