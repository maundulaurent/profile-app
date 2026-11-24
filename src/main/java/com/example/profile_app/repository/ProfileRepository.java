package com.example.profile_app.repository;

import com.example.profile_app.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, String> {
    List<Profile> findByPublishedTrue();
    Optional<Profile> findByIdAndPublishedTrue(String id);
    Optional<Profile> findByOwnerUsername(String ownerUsername);
    List<Profile> findByPublishedTrueAndAvailability(com.example.profile_app.entity.Availability availability);
}