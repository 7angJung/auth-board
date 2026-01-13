package com.jupeter.authboard.domain.user.application;

import com.jupeter.authboard.domain.user.domain.User;
import com.jupeter.authboard.domain.user.dto.UserSignupRequest;
import com.jupeter.authboard.domain.user.dto.UserSignupResponse;
import com.jupeter.authboard.domain.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserSignupResponse signup(UserSignupRequest request) {
        String email = request.email();

        if(userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException("이미 사용 중인 이메일입니다.");
        }

        String encoded = passwordEncoder.encode(request.password());
        User saved = userRepository.save(User.create(email, encoded));

        return new UserSignupResponse(saved.getId(), saved.getEmail());
    }
}
