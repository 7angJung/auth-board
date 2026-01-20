package com.jupeter.authboard.domain.board.api;

import com.jupeter.authboard.domain.board.application.BoardService;
import com.jupeter.authboard.domain.board.dto.BoardCreateRequest;
import com.jupeter.authboard.domain.board.dto.BoardResponse;
import com.jupeter.authboard.global.common.AuthUtils;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BoardResponse create(@Valid @RequestBody BoardCreateRequest request) {
        Long userId = AuthUtils.currentUserId();
        return boardService.create(request, userId);
    }

    @GetMapping
    public List<BoardResponse> findAll() {
        return boardService.findAll();
    }

    @GetMapping("/{id}")
    public BoardResponse findById(@PathVariable Long id) {
        return boardService.findById(id);
    }
}