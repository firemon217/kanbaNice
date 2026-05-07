package com.kanbanice.backend.dto.kanban;

import java.time.LocalDateTime;
import java.util.List;

public record ProjectResponseDTO(
        Long id,
        String name,
        Long companyId,
        LocalDateTime createdAt,
        List<ProjectMemberResponseDTO> members
) {
}

