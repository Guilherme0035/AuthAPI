package com.gui.authAPI.Repository;

import com.gui.authAPI.Entity.Role;
import com.gui.authAPI.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<User> findByName(String name);
}
