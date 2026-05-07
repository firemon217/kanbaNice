package com.kanbanice.backend.dto.kanban;

import jakarta.validation.constraints.NotBlank;

public record BoardCreateDTO(
        @NotBlank String name
) {
}

