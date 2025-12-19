package com.supplychainx.security.repository;

import com.supplychainx.security.model.RefreshToken;
import com.supplychainx.supply_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long > {

    Optional<RefreshToken> findByToken (String token );

    @Query(value = """
      select t from RefreshToken t inner join User u\s
      on t.user.id = u.id\s
      where u.id = :id and (t.revoked = false)\s
      """)
    List<RefreshToken> findAllValidTokenByUser(Long id);

    void deleteByUser(User user);
}
