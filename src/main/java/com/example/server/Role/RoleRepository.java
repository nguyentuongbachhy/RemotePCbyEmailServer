package com.example.server.Role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource(path = "role")
public interface RoleRepository extends JpaRepository<Role, Integer> {
    
    @Query("SELECT r FROM Role r WHERE r.roleName = :roleName")
    public Role findByRoleName(@Param("roleName") String roleName);
}
