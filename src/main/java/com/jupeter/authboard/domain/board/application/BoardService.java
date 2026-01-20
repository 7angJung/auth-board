package com.jupeter.authboard.domain.board.application;

import com.jupeter.authboard.domain.board.application.exception.BoardNotFoundException;
import com.jupeter.authboard.domain.board.domain.Board;
import com.jupeter.authboard.domain.board.dto.BoardCreateRequest;
import com.jupeter.authboard.domain.board.dto.BoardResponse;
import com.jupeter.authboard.domain.board.repository.BoardRepository;
import com.jupeter.authboard.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.jupeter.authboard.domain.board.application.exception.BoardForbiddenException;
import com.jupeter.authboard.domain.board.dto.BoardUpdateRequest;

import java.util.List;

@Service
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    public BoardService(BoardRepository boardRepository, UserRepository userRepository) {
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
    }

    public BoardResponse create(BoardCreateRequest request, Long authorId) {
        var author = userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalStateException("작성자(User)가 존재하지 않습니다."));

        Board saved = boardRepository.save(Board.create(request.title(), request.content(), author));
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<BoardResponse> findAll() {
        return boardRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BoardResponse findById(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardNotFoundException("게시글을 찾을 수 없습니다."));
        return toResponse(board);
    }

    private BoardResponse toResponse(Board board) {
        return new BoardResponse(
                board.getId(),
                board.getTitle(),
                board.getContent(),
                board.getAuthor().getId(),
                board.getCreatedAt()
        );
    }
    public BoardResponse update(Long boardId, BoardUpdateRequest request, Long userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("게시글을 찾을 수 없습니다."));

        if (!board.getAuthor().getId().equals(userId)) {
            throw new BoardForbiddenException("작성자만 수정할 수 있습니다.");
        }

        board.update(request.title(), request.content());
        return toResponse(board);
    }

    public void delete(Long boardId, Long userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("게시글을 찾을 수 없습니다."));

        if (!board.getAuthor().getId().equals(userId)) {
            throw new BoardForbiddenException("작성자만 삭제할 수 있습니다.");
        }

        boardRepository.delete(board);
    }
}