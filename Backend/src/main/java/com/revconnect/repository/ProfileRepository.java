package com.revconnect.repository;

import com.revconnect.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByUserId(Long userId);

    List<Profile> findByFullNameContainingIgnoreCase(String fullName);
}