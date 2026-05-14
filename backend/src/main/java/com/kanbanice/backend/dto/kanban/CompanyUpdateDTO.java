package com.kanbanice.backend.dto.kanban;

import jakarta.validation.constraints.NotBlank;

public record CompanyUpdateDTO(
        @NotBlank String name
) {
}

