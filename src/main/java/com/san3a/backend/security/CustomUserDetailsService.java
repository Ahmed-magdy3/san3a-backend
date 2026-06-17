package com.san3a.backend.security;

import com.san3a.backend.domain.enums.AccountRole;
import com.san3a.backend.repository.AdminRepository;
import com.san3a.backend.repository.TaskerRepository;
import com.san3a.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final TaskerRepository taskerRepository;
    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String email = username.toLowerCase().trim();

        return userRepository.findByEmail(email)
                .<UserDetails>map(user -> new AppUserPrincipal(
                        user.getUserId(),
                        user.getEmail(),
                        user.getPasswordHash(),
                        AccountRole.USER
                ))
                .or(() -> taskerRepository.findByEmail(email)
                        .map(tasker -> new AppUserPrincipal(
                                tasker.getTaskerId(),
                                tasker.getEmail(),
                                tasker.getPasswordHash(),
                                AccountRole.TASKER
                        )))
                .or(() -> adminRepository.findByEmail(email)
                        .map(admin -> new AppUserPrincipal(
                                admin.getAdminId(),
                                admin.getEmail(),
                                admin.getPasswordHash(),
                                AccountRole.ADMIN
                        )))
                .orElseThrow(() -> new UsernameNotFoundException("Invalid email or password"));
    }
}
