package com.barberfind.api.repository;

import com.barberfind.api.domain.UserPaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserPaymentMethodRepository extends JpaRepository<UserPaymentMethod, String> {

    List<UserPaymentMethod> findAllByUserId(String userId);

    Optional<UserPaymentMethod> findByIdAndUserId(String id, String userId);
}