package com.hussein221.repository;

import com.hussein221.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    //select * fron user u where u.email = : email
   Optional<User> findByEmail(String email);
}
