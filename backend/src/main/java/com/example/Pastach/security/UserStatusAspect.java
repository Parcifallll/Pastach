package com.example.Pastach.security;

import com.example.Pastach.exception.UserLockedException;
import com.example.Pastach.model.User;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class UserStatusAspect {

    @Before("@annotation(requireNonLockedUser)")
    public void checkUserStatus(JoinPoint joinPoint, RequireNonLockedUser requireNonLockedUser) {
        log.debug("Checking user status for method: {}", joinPoint.getSignature().getName());

        // get cur user from security context
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return; // @PreAuthorize handle authentication
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof User user) {
            if (user.isLocked()) {
                log.warn("Locked user {} attempted to perform action: {}",
                        user.getId(), joinPoint.getSignature().getName());
                throw new UserLockedException(requireNonLockedUser.message());
            }
        }
    }
}