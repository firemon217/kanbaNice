package com.kanbanice.backend.service.kanban;

import com.kanbanice.backend.dto.kanban.TaskCreateDTO;
import com.kanbanice.backend.dto.kanban.TaskResponseDTO;
import com.kanbanice.backend.dto.kanban.TaskUpdateDTO;
import com.kanbanice.backend.entity.Company;
import com.kanbanice.backend.entity.User;
import com.kanbanice.backend.entity.kanban.*;
import com.kanbanice.backend.entity.type.AuthProviderType;
import com.kanbanice.backend.entity.type.UserType;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("TaskService — интеграционные unit-тесты (H2)")
class TaskServiceTest {

    @Autowired TaskService taskService;
    @MockBean CurrentUserUtil currentUserUtil;
    @MockBean com.kanbanice.backend.service.EmailService emailService;
    @MockBean org.springframework.security.authentication.AuthenticationManager authenticationManager;

    // Используем репозитории через Spring (автоматическое разрешение классов)
    @Autowired org.springframework.data.jpa.repository.JpaRepository<Company, Long> companyJpa;
    @Autowired org.springframework.data.jpa.repository.JpaRepository<User, Long> userJpa;
    @Autowired org.springframework.data.jpa.repository.JpaRepository<KanbanProject, Long> projectJpa;
    @Autowired org.springframework.data.jpa.repository.JpaRepository<KanbanBoard, Long> boardJpa;
    @Autowired org.springframework.data.jpa.repository.JpaRepository<KanbanTask, Long> taskJpa;
    @Autowired org.springframework.data.jpa.repository.JpaRepository<ProjectMember, Long> memberJpa;
    @Autowired jakarta.persistence.EntityManager em;

    private User user;
    private KanbanBoard board;

    @BeforeEach
    void setUp() {
        String uid = UUID.randomUUID().toString().substring(0, 8);

        Company company = companyJpa.save(
                Company.builder().name("TestCo-" + uid).build());

        user = userJpa.save(User.builder()
                .name("Alice").username("alice-" + uid)
                .email("alice-" + uid + "@t.com")
                .password("hash")
                .providerType(AuthProviderType.EMAIL)
                .userType(UserType.WORKER)
                .company(company).roles(Set.of()).build());

        KanbanProject project = projectJpa.save(KanbanProject.builder()
                .name("Project").company(company).build());

        memberJpa.save(ProjectMember.builder()
                .project(project).user(user).role(ProjectMemberRole.WORKER).build());

        board = boardJpa.save(KanbanBoard.builder()
                .name("Sprint 1").project(project).build());

        em.flush();
        em.clear();
        user = userJpa.findById(user.getId()).orElseThrow();
        board = boardJpa.findById(board.getId()).orElseThrow();

        when(currentUserUtil.getCurrentUser()).thenReturn(user);
    }

    // ─── createTask ───────────────────────────────────────────────────

    @Test
    @DisplayName("createTask: создаёт задачу со статусом TODO по умолчанию")
    void createTask_defaultStatusTodo() {
        TaskResponseDTO result = taskService.createTask(board.getId(),
                new TaskCreateDTO("New Task", "Desc", null));

        assertThat(result.title()).isEqualTo("New Task");
        assertThat(result.status()).isEqualTo(TaskStatus.TODO);
        assertThat(result.boardId()).isEqualTo(board.getId());
    }

    @Test
    @DisplayName("createTask: явный статус DONE сохраняется")
    void createTask_explicitStatusDone() {
        TaskResponseDTO result = taskService.createTask(board.getId(),
                new TaskCreateDTO("Done Task", null, TaskStatus.DONE));

        assertThat(result.status()).isEqualTo(TaskStatus.DONE);
    }

    @Test
    @DisplayName("createTask: EntityNotFoundException при несуществующей доске")
    void createTask_boardNotFound() {
        assertThatThrownBy(() -> taskService.createTask(99999L,
                new TaskCreateDTO("X", null, null)))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Board not found");
    }

    @Test
    @DisplayName("createTask: IllegalStateException при другой компании")
    void createTask_differentCompany() {
        String uid2 = UUID.randomUUID().toString().substring(0, 8);
        Company other = companyJpa.save(Company.builder().name("Other-" + uid2).build());
        user.setCompany(other);

        assertThatThrownBy(() -> taskService.createTask(board.getId(),
                new TaskCreateDTO("T", null, null)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("different company");
    }

    // ─── getTask ──────────────────────────────────────────────────────

    @Test
    @DisplayName("getTask: успешно возвращает задачу")
    void getTask_success() {
        TaskResponseDTO created = taskService.createTask(board.getId(),
                new TaskCreateDTO("My Task", "Desc", null));

        TaskResponseDTO result = taskService.getTask(created.id());

        assertThat(result.title()).isEqualTo("My Task");
    }

    @Test
    @DisplayName("getTask: EntityNotFoundException при несуществующей задаче")
    void getTask_notFound() {
        assertThatThrownBy(() -> taskService.getTask(99999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Task not found");
    }

    // ─── listTasks ────────────────────────────────────────────────────

    @Test
    @DisplayName("listTasks: возвращает все задачи доски")
    void listTasks_success() {
        taskService.createTask(board.getId(), new TaskCreateDTO("Task 1", null, null));
        taskService.createTask(board.getId(), new TaskCreateDTO("Task 2", null, null));

        var result = taskService.listTasks(board.getId());

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("listTasks: EntityNotFoundException при несуществующей доске")
    void listTasks_boardNotFound() {
        assertThatThrownBy(() -> taskService.listTasks(99999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    // ─── updateTask ───────────────────────────────────────────────────

    @Test
    @DisplayName("updateTask: обновляет заголовок")
    void updateTask_updateTitle() {
        TaskResponseDTO task = taskService.createTask(board.getId(),
                new TaskCreateDTO("Old", null, null));

        TaskResponseDTO result = taskService.updateTask(task.id(),
                new TaskUpdateDTO("New Title", null, null));

        assertThat(result.title()).isEqualTo("New Title");
    }

    @Test
    @DisplayName("updateTask: меняет статус и записывает username")
    void updateTask_changeStatus() {
        TaskResponseDTO task = taskService.createTask(board.getId(),
                new TaskCreateDTO("Task", null, TaskStatus.TODO));

        TaskResponseDTO result = taskService.updateTask(task.id(),
                new TaskUpdateDTO(null, null, TaskStatus.DONE));

        assertThat(result.status()).isEqualTo(TaskStatus.DONE);
        assertThat(result.statusChangedBy()).isEqualTo(user.getUsername());
    }

    @Test
    @DisplayName("updateTask: EntityNotFoundException при несуществующей задаче")
    void updateTask_notFound() {
        assertThatThrownBy(() -> taskService.updateTask(99999L,
                new TaskUpdateDTO("X", null, null)))
                .isInstanceOf(EntityNotFoundException.class);
    }

    // ─── deleteTask ───────────────────────────────────────────────────

    @Test
    @DisplayName("deleteTask: успешно удаляет задачу")
    void deleteTask_success() {
        TaskResponseDTO task = taskService.createTask(board.getId(),
                new TaskCreateDTO("To Delete", null, null));

        taskService.deleteTask(task.id());

        assertThatThrownBy(() -> taskService.getTask(task.id()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("deleteTask: EntityNotFoundException при несуществующей задаче")
    void deleteTask_notFound() {
        assertThatThrownBy(() -> taskService.deleteTask(99999L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
