package com.jupeter.authboard.domain.auth.api;

import com.jupeter.authboard.domain.auth.jwt.JwtProperties;
import com.jupeter.authboard.domain.auth.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthTestControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private JwtProperties jwtProperties;

    @Test
    @DisplayName("토큰 없이 /api/me 요청하면 401")
    void me_withoutToken_401() throws Exception {
        mockMvc.perform(get("/api/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("유효한 토큰으로 /api/me 요청하면 200")
    void me_withToken_200() throws Exception {
        String token = jwtTokenProvider.createAccessToken(1L, "test@example.com", "USER");

        mockMvc.perform(get("/api/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }
}
