package com.jupeter.authboard.domain.user.application;

import com.jupeter.authboard.domain.user.dto.UserSignupRequest;
import com.jupeter.authboard.domain.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
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

    @Test
    @DisplayName("회원가입 성공: 이메일을 저장하고 비밀번호는 해시로 저장된다")
    void signup_success() {
        // given
        UserService userService = new UserService(userRepository, passwordEncoder);
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
        UserService userService = new UserService(userRepository, passwordEncoder);
        userService.signup(new UserSignupRequest("dup@example.com", "password1234"));

        // when & then
        assertThatThrownBy(() ->
                userService.signup(new UserSignupRequest("dup@example.com", "password9999"))
        ).isInstanceOf(DuplicateEmailException.class);
    }
}