package com.jupeter.authboard.domain.board.dto;

import java.time.LocalDateTime;

public record BoardResponse(
        Long id,
        String title,
        String content,
        Long authorId,
        LocalDateTime createdAt
) {}