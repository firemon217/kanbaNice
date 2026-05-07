package com.kanbanice.backend.dto.kanban;

import jakarta.validation.constraints.NotBlank;

public record CompanyCreateDTO(
        @NotBlank String name
) {
}

