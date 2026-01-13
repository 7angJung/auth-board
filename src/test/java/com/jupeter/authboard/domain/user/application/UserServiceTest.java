package com.jupeter.authboard.domain.user.application;

import com.jupeter.authboard.domain.auth.jwt.JwtProperties;
import com.jupeter.authboard.domain.auth.jwt.JwtTokenProvider;
import com.jupeter.authboard.domain.user.application.exception.InvalidPasswordException;
import com.jupeter.authboard.domain.user.application.exception.UserNotFoundException;
import com.jupeter.authboard.domain.user.dto.UserLoginRequest;
import com.jupeter.authboard.domain.user.dto.UserSignupRequest;
import com.jupeter.authboard.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import(com.jupeter.authboard.global.config.SecurityBeanConfig.class)
class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider =
            new JwtTokenProvider(new JwtProperties(
                    "dev-secret-key-change-this-please-dev-secret-key-change-this",
                    60
            ));

    @Test
    @DisplayName("회원가입 성공: 이메일을 저장하고 비밀번호는 해시로 저장된다")
    void signup_success() {
        // given
        UserService userService = new UserService(userRepository, passwordEncoder, jwtTokenProvider);
        UserSignupRequest req = new UserSignupRequest("test@example.com", "password1234");

        // when
        var res = userService.signup(req);

        // then
        var saved = userRepository.findByEmail("test@example.com").orElseThrow();
        assertThat(res.email()).isEqualTo("test@example.com");
        assertThat(saved.getPassword()).isNotEqualTo("password1234");
        assertThat(passwordEncoder.matches("password1234", saved.getPassword())).isTrue();
    }

    @Test
    @DisplayName("회원가입 실패: 이메일이 중복이면 예외가 발생한다")
    void signup_duplicateEmail() {
        // given
        UserService userService = new UserService(userRepository, passwordEncoder, jwtTokenProvider);
        userService.signup(new UserSignupRequest("dup@example.com", "password1234"));

        // when & then
        assertThatThrownBy(() ->
                userService.signup(new UserSignupRequest("dup@example.com", "password9999"))
        ).isInstanceOf(DuplicateEmailException.class);
    }

    @Test
    @DisplayName("로그인 성공: access token이 발급된다")
    void login_success() {
        // given
        UserService userService = new UserService(userRepository, passwordEncoder, jwtTokenProvider);
        userService.signup(new UserSignupRequest("test@example.com", "password1234"));

        // when
        var res = userService.login(new UserLoginRequest("test@example.com", "password1234"));

        // then
        assertThat(res.accessToken()).isNotBlank();
        assertThat(res.accessToken().split("\\.")).hasSize(3); // JWT 형태(헤더.페이로드.서명)
    }

    @Test
    @DisplayName("로그인 실패: 존재하지 않는 이메일이면 예외가 발생한다")
    void login_userNotFound() {
        // given
        UserService userService = new UserService(userRepository, passwordEncoder, jwtTokenProvider);

        // when & then
        assertThatThrownBy(() ->
                userService.login(new UserLoginRequest("nope@example.com", "password1234"))
        ).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("로그인 실패: 비밀번호가 틀리면 예외가 발생한다")
    void login_invalidPassword() {
        // given
        UserService userService = new UserService(userRepository, passwordEncoder, jwtTokenProvider);
        userService.signup(new UserSignupRequest("test@example.com", "password1234"));

        // when & then
        assertThatThrownBy(() ->
                userService.login(new UserLoginRequest("test@example.com", "wrongpass"))
        ).isInstanceOf(InvalidPasswordException.class);
    }
}