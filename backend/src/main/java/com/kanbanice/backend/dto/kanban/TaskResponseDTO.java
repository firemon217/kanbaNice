package com.kanbanice.backend.dto.kanban;

import com.kanbanice.backend.entity.kanban.TaskStatus;

import java.time.LocalDateTime;

public record TaskResponseDTO(
        Long id,
        Long boardId,
        String title,
        String description,
        TaskStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

