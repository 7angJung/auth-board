package com.jupeter.authboard.domain.board.repository;

import com.jupeter.authboard.domain.board.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}