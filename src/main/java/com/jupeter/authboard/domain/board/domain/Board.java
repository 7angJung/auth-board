package com.jupeter.authboard.domain.board.domain;

import com.jupeter.authboard.domain.user.domain.User;
import com.jupeter.authboard.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "boards")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_boards_author"))
    private User author;

    private Board(String title, String content, User author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }

    public static Board create(String title, String content, User author) {
        return new Board(title, content, author);
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}