package com.jupeter.authboard.domain.board.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jupeter.authboard.domain.auth.jwt.JwtTokenProvider;
import com.jupeter.authboard.domain.board.dto.BoardCreateRequest;
import com.jupeter.authboard.domain.user.domain.User;
import com.jupeter.authboard.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import com.jupeter.authboard.domain.board.repository.BoardRepository;
import com.jupeter.authboard.domain.board.domain.Board;
import com.jupeter.authboard.domain.board.dto.BoardUpdateRequest;
import org.springframework.http.HttpMethod;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BoardApiIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtTokenProvider jwtTokenProvider;
    @Autowired private BoardRepository boardRepository;

    @Test
    @DisplayName("토큰 없이 게시글 작성 요청하면 401")
    void create_withoutToken_401() throws Exception {
        var req = new BoardCreateRequest("제목", "내용");

        mockMvc.perform(post("/api/boards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("토큰이 있으면 게시글 작성이 되고 201을 반환한다")
    void create_withToken_201() throws Exception {
        // given: 유저 생성 + 토큰 발급
        User user = userRepository.save(User.create("author@test.com", passwordEncoder.encode("password1234")));
        String token = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail(), user.getRole().name());

        var req = new BoardCreateRequest("제목", "내용");

        // when & then
        mockMvc.perform(post("/api/boards")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("제목"))
                .andExpect(jsonPath("$.content").value("내용"))
                .andExpect(jsonPath("$.authorId").value(user.getId()));
    }

    @Test
    @DisplayName("게시글 목록 조회는 200을 반환한다")
    void findAll_200() throws Exception {
        mockMvc.perform(get("/api/boards"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("토큰 없이 게시글 수정 요청하면 401")
    void update_withoutToken_401() throws Exception {
        var req = new BoardUpdateRequest("수정제목", "수정내용");

        mockMvc.perform(put("/api/boards/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("작성자 토큰으로 게시글 수정하면 200")
    void update_asAuthor_200() throws Exception {
        User author = userRepository.save(User.create("author2@test.com", passwordEncoder.encode("password1234")));
        Board board = boardRepository.save(Board.create("기존제목", "기존내용", author));
        String token = jwtTokenProvider.createAccessToken(author.getId(), author.getEmail(), author.getRole().name());

        var req = new BoardUpdateRequest("수정제목", "수정내용");

        mockMvc.perform(put("/api/boards/{id}", board.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("수정제목"))
                .andExpect(jsonPath("$.content").value("수정내용"))
                .andExpect(jsonPath("$.authorId").value(author.getId()));
    }

    @Test
    @DisplayName("작성자가 아닌 토큰으로 게시글 수정하면 403")
    void update_notAuthor_403() throws Exception {
        User author = userRepository.save(User.create("author3@test.com", passwordEncoder.encode("password1234")));
        Board board = boardRepository.save(Board.create("기존제목", "기존내용", author));

        User other = userRepository.save(User.create("other@test.com", passwordEncoder.encode("password1234")));
        String otherToken = jwtTokenProvider.createAccessToken(other.getId(), other.getEmail(), other.getRole().name());

        var req = new BoardUpdateRequest("수정제목", "수정내용");

        mockMvc.perform(put("/api/boards/{id}", board.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + otherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("작성자 토큰으로 게시글 삭제하면 204")
    void delete_asAuthor_204() throws Exception {
        User author = userRepository.save(User.create("author4@test.com", passwordEncoder.encode("password1234")));
        Board board = boardRepository.save(Board.create("제목", "내용", author));
        String token = jwtTokenProvider.createAccessToken(author.getId(), author.getEmail(), author.getRole().name());

        mockMvc.perform(delete("/api/boards/{id}", board.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("작성자가 아닌 토큰으로 게시글 삭제하면 403")
    void delete_notAuthor_403() throws Exception {
        User author = userRepository.save(User.create("author5@test.com", passwordEncoder.encode("password1234")));
        Board board = boardRepository.save(Board.create("제목", "내용", author));

        User other = userRepository.save(User.create("other2@test.com", passwordEncoder.encode("password1234")));
        String otherToken = jwtTokenProvider.createAccessToken(other.getId(), other.getEmail(), other.getRole().name());

        mockMvc.perform(delete("/api/boards/{id}", board.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + otherToken))
                .andExpect(status().isForbidden());
    }

}