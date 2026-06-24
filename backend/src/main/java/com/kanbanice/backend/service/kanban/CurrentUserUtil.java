package com.kanbanice.backend.service.kanban;

import com.kanbanice.backend.entity.User;
import com.kanbanice.backend.entity.UserPrinciple;
import com.kanbanice.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrentUserUtil {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new RuntimeException("User not authenticated");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrinciple userPrinciple) {
            return userPrinciple.getUser();
        }

        // Fallback: try oauth attribute email is not handled here; keep consistent with existing controllers
        throw new RuntimeException("Unsupported principal type: " + principal.getClass().getName());
    }
}
