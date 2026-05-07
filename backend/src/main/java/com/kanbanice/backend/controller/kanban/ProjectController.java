package com.kanbanice.backend.controller.kanban;

import com.kanbanice.backend.dto.kanban.ProjectCreateDTO;
import com.kanbanice.backend.dto.kanban.ProjectResponseDTO;
import com.kanbanice.backend.service.kanban.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<List<ProjectResponseDTO>> listMyProjects() {
        return ResponseEntity.ok(projectService.listMyProjects());
    }

    @PostMapping
    public ResponseEntity<ProjectResponseDTO> createProject(@Valid @RequestBody ProjectCreateDTO dto) {
        return ResponseEntity.ok(projectService.createProject(dto));
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponseDTO> getProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.getProject(projectId));
    }

    @PostMapping("/{projectId}/members/{userId}")
    public ResponseEntity<ProjectResponseDTO> addWorkerToProject(
            @PathVariable Long projectId,
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(projectService.addWorkerToProject(projectId, userId));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.noContent().build();
    }
}
