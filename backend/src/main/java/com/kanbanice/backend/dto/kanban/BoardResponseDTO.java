package com.kanbanice.backend.dto.kanban;

import java.time.LocalDateTime;

public record BoardResponseDTO(
        Long id,
        String name,
        Long projectId,
        LocalDateTime createdAt
) {
}

