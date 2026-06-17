package com.san3a.backend.auth.service;

import com.san3a.backend.auth.dto.*;
import com.san3a.backend.common.exception.BusinessException;
import com.san3a.backend.domain.entity.Tasker;
import com.san3a.backend.domain.entity.User;
import com.san3a.backend.domain.enums.AccountRole;
import com.san3a.backend.repository.AdminRepository;
import com.san3a.backend.repository.TaskerRepository;
import com.san3a.backend.repository.UserRepository;
import com.san3a.backend.security.AppUserPrincipal;
import com.san3a.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final TaskerRepository taskerRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse registerUser(RegisterUserRequest request) {
        String email = normalizeEmail(request.email());
        assertEmailAvailable(email);

        User user = new User();
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setEmail(email);
        user.setPhone(request.phone().trim());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user = userRepository.save(user);

        AppUserPrincipal principal = new AppUserPrincipal(user.getUserId(), user.getEmail(), user.getPasswordHash(), AccountRole.USER);
        return buildResponse(principal);
    }

    public AuthResponse registerTasker(RegisterTaskerRequest request) {
        String email = normalizeEmail(request.email());
        assertEmailAvailable(email);

        Tasker tasker = new Tasker();
        tasker.setFirstName(request.firstName().trim());
        tasker.setLastName(request.lastName().trim());
        tasker.setEmail(email);
        tasker.setPhone(request.phone().trim());
        tasker.setPasswordHash(passwordEncoder.encode(request.password()));
        tasker.setVerified(false);
        tasker = taskerRepository.save(tasker);

        AppUserPrincipal principal = new AppUserPrincipal(tasker.getTaskerId(), tasker.getEmail(), tasker.getPasswordHash(), AccountRole.TASKER);
        return buildResponse(principal);
    }

    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.password())
            );

            AppUserPrincipal principal = (AppUserPrincipal) authentication.getPrincipal();
            return buildResponse(principal);

        } catch (BadCredentialsException e) {
            throw new BusinessException("Invalid email or password");
        }
    }

    private void assertEmailAvailable(String email) {
        boolean exists = userRepository.existsByEmail(email)
                || taskerRepository.existsByEmail(email)
                || adminRepository.existsByEmail(email);

        if (exists) {
            throw new BusinessException("Email is already in use");
        }
    }

    private String normalizeEmail(String email) {
        return email.toLowerCase().trim();
    }

    private AuthResponse buildResponse(AppUserPrincipal principal) {
        String token = jwtService.generateToken(principal);
        return new AuthResponse(token, "Bearer", principal.getActorId(), principal.getRole(), principal.getEmail());
    }
}
