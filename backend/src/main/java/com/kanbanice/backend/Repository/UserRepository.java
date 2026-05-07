package com.kanbanice.backend.Repository;

import com.kanbanice.backend.entity.type.AuthProviderType;
import com.kanbanice.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByResetToken(String resetToken);
    Optional<User> findByEmailVerificationToken(String token);
    Optional<User> findByProviderTypeAndProviderId(AuthProviderType providerType, String providerId);
    List<User> findAllByCompany_Id(Long companyId);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

}
