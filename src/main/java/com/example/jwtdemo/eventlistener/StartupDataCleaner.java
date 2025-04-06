package com.example.jwtdemo.eventlistener;

import com.example.jwtdemo.repository.UserRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupDataCleaner {

    private final UserRepository userRepository;

    public StartupDataCleaner(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void clearTableOnStartup() {
        userRepository.deleteAll();
        System.out.println("✅ Table cleared at application startup.");
    }
}
