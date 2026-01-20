package com.jupeter.authboard.domain.board.repository;

import com.jupeter.authboard.domain.board.domain.Board;
import com.jupeter.authboard.domain.user.domain.User;
import com.jupeter.authboard.domain.user.repository.UserRepository;
import com.jupeter.authboard.global.config.SecurityBeanConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(SecurityBeanConfig.class)
class BoardRepositoryTest {

    @Autowired private BoardRepository boardRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("게시글을 작성자(User)와 함께 저장/조회할 수 있다")
    void save_and_find() {
        // given
        User author = userRepository.save(User.create("author@test.com", passwordEncoder.encode("password1234")));
        Board board = boardRepository.save(Board.create("제목", "내용", author));

        // when
        Board found = boardRepository.findById(board.getId()).orElseThrow();

        // then
        assertThat(found.getTitle()).isEqualTo("제목");
        assertThat(found.getContent()).isEqualTo("내용");
        assertThat(found.getAuthor().getId()).isEqualTo(author.getId());
    }
}