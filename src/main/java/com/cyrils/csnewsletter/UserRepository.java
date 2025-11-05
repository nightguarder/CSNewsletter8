package com.cyrils.csnewsletter;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    // Convenience query to look up a user by email. Used by job workers that need
    // to detect duplicates based on the subscriber's email address.
    Optional<User> findByEmail(String email);

}
