package com.kanbanice.backend.dto.kanban;
import com.kanbanice.backend.dto.UserResponseDTO;
import java.util.List;

public record CompanyResponseDTO(
        Long id,
        String name,
        List<UserResponseDTO> users 
) {
}

