package com.kanbanice.backend.controller.kanban;

import com.kanbanice.backend.dto.kanban.TaskCreateDTO;
import com.kanbanice.backend.dto.kanban.TaskResponseDTO;
import com.kanbanice.backend.dto.kanban.TaskUpdateDTO;
import com.kanbanice.backend.service.kanban.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/boards/{boardId}/tasks")
    public ResponseEntity<List<TaskResponseDTO>> listTasks(@PathVariable Long boardId) {
        return ResponseEntity.ok(taskService.listTasks(boardId));
    }

    @PostMapping("/boards/{boardId}/tasks")
    public ResponseEntity<TaskResponseDTO> createTask(
            @PathVariable Long boardId,
            @Valid @RequestBody TaskCreateDTO dto
    ) {
        return ResponseEntity.ok(taskService.createTask(boardId, dto));
    }

    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<TaskResponseDTO> getTask(@PathVariable Long taskId) {
        return ResponseEntity.ok(taskService.getTask(taskId));
    }

    @PutMapping("/tasks/{taskId}")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable Long taskId,
            @RequestBody TaskUpdateDTO dto
    ) {
        return ResponseEntity.ok(taskService.updateTask(taskId, dto));
    }

    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }
}
