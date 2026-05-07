package com.kanbanice.backend.dto.kanban;

import com.kanbanice.backend.entity.kanban.ProjectMemberRole;

public record ProjectMemberResponseDTO(
        Long userId,
        String username,
        String name,
        ProjectMemberRole role
) {
}

