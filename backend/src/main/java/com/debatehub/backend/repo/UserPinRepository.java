package com.debatehub.backend.repo;

import com.debatehub.backend.domain.UserPin;
import com.debatehub.backend.domain.ids.UserPinId;
import com.debatehub.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPinRepository extends JpaRepository<UserPin, UserPinId> {
    List<UserPin> findByUser(User user);
}
