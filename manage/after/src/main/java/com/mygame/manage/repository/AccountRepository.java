package com.mygame.manage.repository;

import com.mygame.manage.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    Optional<AccountEntity> findByUsername(String username);

    long countByRoleAndEnabled(String role, Boolean enabled);
}
