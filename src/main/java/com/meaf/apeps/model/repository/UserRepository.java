package com.meaf.apeps.model.repository;

import com.meaf.apeps.model.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends IRepository<User> {

  @Query("SELECT u FROM User u where u.name = :name and u.password = :password")
  User checkUser(@Param("name") String name, @Param("password") String password);

  @Query("SELECT u FROM User u where u.name = :name")
  User checkUsername(@Param("name") String name);
}
