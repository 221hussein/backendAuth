package com.hussein221.repository;

import com.hussein221.model.User;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    //select * fron user u where u.email = : email
   Optional<User> findByEmail(String email);

   @Query("""
       select u.* from user u inner join token t on u.id = t.user  
       where u.id= :id and t.refresh_token = :refreshToken and t.expired_at >= :expiredAt
   """)

   Optional<User> findByIdAndTokensRefreshTokenAndTokensExpiratedAtGreaterThan
           (Long id, String refreshToken, LocalDateTime expiredAt);

   @Query("""
    select u* from user u inner join password_recovery pr on u.id = pr.user
    where pr.token = :token
""")
   Optional<User> findByPasswordRecoveriesTokens(String token);
}
