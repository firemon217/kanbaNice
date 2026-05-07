package com.kanbanice.backend.controller.kanban;

import com.kanbanice.backend.dto.kanban.BoardCreateDTO;
import com.kanbanice.backend.dto.kanban.BoardResponseDTO;
import com.kanbanice.backend.service.kanban.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/{projectId}/boards")
    public ResponseEntity<List<BoardResponseDTO>> listBoards(@PathVariable Long projectId) {
        return ResponseEntity.ok(boardService.listBoards(projectId));
    }

    @PostMapping("/{projectId}/boards")
    public ResponseEntity<BoardResponseDTO> createBoard(
            @PathVariable Long projectId,
            @Valid @RequestBody BoardCreateDTO dto
    ) {
        return ResponseEntity.ok(boardService.createBoard(projectId, dto));
    }

    @DeleteMapping("/boards/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long boardId) {
        boardService.deleteBoard(boardId);
        return ResponseEntity.noContent().build();
    }
}
