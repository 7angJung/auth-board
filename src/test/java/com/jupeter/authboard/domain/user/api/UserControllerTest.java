package com.jupeter.authboard.domain.user.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jupeter.authboard.domain.user.application.UserService;
import com.jupeter.authboard.domain.user.dto.UserLoginRequest;
import com.jupeter.authboard.domain.user.dto.UserLoginResponse;
import com.jupeter.authboard.domain.user.dto.UserSignupRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("회원가입 실패: 이메일 형식이 아니면 400 Bad Request.")
    void signup_invalidEmail() throws Exception {
        var req = new UserSignupRequest("not-email", "password1234");

        mockMvc.perform(post("/api/users/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인 성공: 200 OK와 accessToken을 반환한다")
    void login_success() throws Exception {
        Mockito.when(userService.login(any(UserLoginRequest.class)))
                .thenReturn(new UserLoginResponse("header.payload.signature"));

        var req = new UserLoginRequest("test@example.com", "password1234");

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("header.payload.signature"));
    }

    @Test
    @DisplayName("로그인 실패: 이메일 형식이 아니면 400 Bad Request")
    void login_invalidEmail() throws Exception {
        var req = new UserLoginRequest("not-email", "password1234");

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }
}