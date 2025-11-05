package com.cyrils.csnewsletter;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

// This class was the previous in-memory repository. It is kept for reference but is not a Spring bean
// now that we use Spring Data JPA. Removing @Component and the implemented interface prevents
// conflicts with the new `UserRepository extends JpaRepository`.
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InMemoryUserRepository {

    private final static Logger log = LoggerFactory.getLogger(InMemoryUserRepository.class);

    private final Map<String, User> store = new ConcurrentHashMap<>();

    public User save(User user) {
        log.debug("(InMemory) Saving user with id={} email={}", user.getUserId(), user.getEmail());
        store.put(user.getUserId(), user);
        log.info("(InMemory) User saved: {}", user.getUserId());
        return user;
    }

    public Optional<User> findById(String userId) {
        return Optional.ofNullable(store.get(userId));
    }
}
