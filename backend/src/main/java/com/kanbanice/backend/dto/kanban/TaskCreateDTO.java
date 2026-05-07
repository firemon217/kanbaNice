package com.kanbanice.backend.dto.kanban;

import com.kanbanice.backend.entity.kanban.TaskStatus;
import jakarta.validation.constraints.NotBlank;

public record TaskCreateDTO(
        @NotBlank String title,
        String description,
        TaskStatus status
) {
}

