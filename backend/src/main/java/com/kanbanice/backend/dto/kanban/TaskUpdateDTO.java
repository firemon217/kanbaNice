package com.kanbanice.backend.dto.kanban;

import com.kanbanice.backend.entity.kanban.TaskStatus;

public record TaskUpdateDTO(
        String title,
        String description,
        TaskStatus status
) {
}

