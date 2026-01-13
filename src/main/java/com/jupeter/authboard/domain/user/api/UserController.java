package com.jupeter.authboard.domain.user.api;

import com.jupeter.authboard.domain.user.application.UserService;
import com.jupeter.authboard.domain.user.dto.UserSignupRequest;
import com.jupeter.authboard.domain.user.dto.UserSignupResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public UserSignupResponse signup(@Valid @RequestBody UserSignupRequest request) {
        return userService.signup(request);
    }
}