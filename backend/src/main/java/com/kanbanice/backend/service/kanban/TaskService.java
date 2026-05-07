package com.kanbanice.backend.service.kanban;

import com.kanbanice.backend.dto.kanban.*;
import com.kanbanice.backend.entity.User;
import com.kanbanice.backend.entity.kanban.*;
import com.kanbanice.backend.entity.type.UserType;
import com.kanbanice.backend.repository.kanban.KanbanBoardRepository;
import com.kanbanice.backend.repository.kanban.KanbanTaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final CurrentUserUtil currentUserUtil;
    private final KanbanTaskRepository taskRepository;
    private final KanbanBoardRepository boardRepository;

    @Transactional(readOnly = true)
    public List<TaskResponseDTO> listTasks(Long boardId) {
        User currentUser = currentUserUtil.getCurrentUser();

        KanbanBoard board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found"));

        KanbanProject project = board.getProject();
        assertSameCompany(currentUser, project.getCompany());
        assertMember(currentUser, project);

        return taskRepository.findAllByBoard(board).stream()
                .map(this::toTaskResponse)
                .toList();
    }

    @Transactional
    public TaskResponseDTO createTask(Long boardId, TaskCreateDTO dto) {
        User currentUser = currentUserUtil.getCurrentUser();

        KanbanBoard board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found"));

        KanbanProject project = board.getProject();
        assertSameCompany(currentUser, project.getCompany());
        assertProjectLeader(currentUser, project);

        KanbanTask task = KanbanTask.builder()
                .title(dto.title())
                .description(dto.description())
                .status(dto.status() != null ? dto.status() : TaskStatus.TODO)
                .board(board)
                .build();

        KanbanTask saved = taskRepository.save(task);
        return toTaskResponse(saved);
    }

    @Transactional(readOnly = true)
    public TaskResponseDTO getTask(Long taskId) {
        User currentUser = currentUserUtil.getCurrentUser();

        KanbanTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        KanbanProject project = task.getBoard().getProject();
        assertSameCompany(currentUser, project.getCompany());
        assertMember(currentUser, project);

        return toTaskResponse(task);
    }

    @Transactional
    public TaskResponseDTO updateTask(Long taskId, TaskUpdateDTO dto) {
        User currentUser = currentUserUtil.getCurrentUser();

        KanbanTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        KanbanProject project = task.getBoard().getProject();
        assertSameCompany(currentUser, project.getCompany());
        ProjectMember member = assertMemberAndGetRole(currentUser, project);

        boolean isProjectLeader = currentUser.getUserType() == UserType.LEADER
                && member.getRole() == ProjectMemberRole.LEADER;
        if (!isProjectLeader && currentUser.getUserType() == UserType.WORKER) {
            if (dto.title() != null || dto.description() != null) {
                throw new IllegalStateException("WORKER can only change task status");
            }
            if (dto.status() == null) {
                throw new IllegalStateException("Status is required for WORKER task update");
            }
        }

        if (dto.title() != null) {
            task.setTitle(dto.title());
        }
        if (dto.description() != null) {
            task.setDescription(dto.description());
        }
        if (dto.status() != null && dto.status() != task.getStatus()) {
            task.setStatus(dto.status());
            task.setStatusChangedBy(currentUser.getUsername());
        }

        KanbanTask saved = taskRepository.save(task);
        return toTaskResponse(saved);
    }

    @Transactional
    public void deleteTask(Long taskId) {
        User currentUser = currentUserUtil.getCurrentUser();

        KanbanTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        KanbanProject project = task.getBoard().getProject();
        assertSameCompany(currentUser, project.getCompany());

        assertProjectLeader(currentUser, project);

        taskRepository.delete(task);
    }

    private void assertSameCompany(User currentUser, com.kanbanice.backend.entity.Company projectCompany) {
        if (projectCompany == null || currentUser.getCompany() == null || !projectCompany.getId().equals(currentUser.getCompany().getId())) {
            throw new IllegalStateException("Forbidden: different company");
        }
    }

    private void assertMember(User currentUser, KanbanProject project) {
        boolean ok = project.getMembers().stream()
                .anyMatch(m -> m.getUser().getId().equals(currentUser.getId()));
        if (!ok) throw new IllegalStateException("Forbidden: not a project member");
    }

    private ProjectMember assertMemberAndGetRole(User currentUser, KanbanProject project) {
        return project.getMembers().stream()
                .filter(m -> m.getUser().getId().equals(currentUser.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Forbidden: not a project member"));
    }

    private void assertProjectLeader(User currentUser, KanbanProject project) {
        if (currentUser.getUserType() != UserType.LEADER) {
            throw new IllegalStateException("Only LEADER can manage tasks");
        }
        if (assertMemberAndGetRole(currentUser, project).getRole() != ProjectMemberRole.LEADER) {
            throw new IllegalStateException("Only project leader can manage tasks");
        }
    }

    private TaskResponseDTO toTaskResponse(KanbanTask task) {
        return new TaskResponseDTO(
                task.getId(),
                task.getBoard().getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getStatusChangedBy(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}
