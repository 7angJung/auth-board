package com.jupeter.authboard.domain.user.application;

import com.jupeter.authboard.domain.auth.jwt.JwtTokenProvider;
import com.jupeter.authboard.domain.user.application.exception.InvalidPasswordException;
import com.jupeter.authboard.domain.user.application.exception.UserNotFoundException;
import com.jupeter.authboard.domain.user.domain.User;
import com.jupeter.authboard.domain.user.dto.UserLoginRequest;
import com.jupeter.authboard.domain.user.dto.UserLoginResponse;
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
    private final JwtTokenProvider jwtTokenProvider;
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
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

    public UserLoginResponse login(UserLoginRequest request) {
        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 이메일입니다."));

        if(!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidPasswordException("비밀번호가 올바르지 않습니다.");
        }

        String token = jwtTokenProvider.createAccessToken(
                user.getId(), user.getEmail(), user.getRole().name()
        );

        return new UserLoginResponse(token);
    }
}
