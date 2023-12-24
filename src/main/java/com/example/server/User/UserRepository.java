package com.example.server.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource(path = "user-entity")
public interface UserRepository extends JpaRepository<UserInformation, Integer> {

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM UserInformation u WHERE u.email = :email")
    boolean existsByEmail(@Param("email") String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM UserInformation u WHERE u.username = :username")
    boolean existsByUsername(@Param("username") String username);

    @Query("SELECT u FROM UserInformation u WHERE u.email = :email")
    UserInformation findByEmail(@Param("email") String email);

    @Query("SELECT u FROM UserInformation u WHERE u.username = :username")
    UserInformation findByUsername(@Param("username") String username);
}
