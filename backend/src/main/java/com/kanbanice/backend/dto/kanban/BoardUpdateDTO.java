package com.kanbanice.backend.dto.kanban;

import jakarta.validation.constraints.NotBlank;

public record BoardUpdateDTO(
        @NotBlank String name
) {
}
