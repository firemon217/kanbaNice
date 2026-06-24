package com.kanbanice.backend.service.kanban;

import com.kanbanice.backend.dto.kanban.ProjectCreateDTO;
import com.kanbanice.backend.dto.kanban.ProjectResponseDTO;
import com.kanbanice.backend.entity.Company;
import com.kanbanice.backend.entity.User;
import com.kanbanice.backend.entity.kanban.KanbanProject;
import com.kanbanice.backend.entity.kanban.ProjectMember;
import com.kanbanice.backend.entity.kanban.ProjectMemberRole;
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

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("ProjectService — интеграционные unit-тесты (H2)")
class ProjectServiceTest {

    @Autowired ProjectService projectService;
    @MockBean CurrentUserUtil currentUserUtil;
    @MockBean com.kanbanice.backend.service.EmailService emailService;
    @MockBean org.springframework.security.authentication.AuthenticationManager authenticationManager;

    @Autowired org.springframework.data.jpa.repository.JpaRepository<Company, Long> companyJpa;
    @Autowired org.springframework.data.jpa.repository.JpaRepository<User, Long> userJpa;
    @Autowired org.springframework.data.jpa.repository.JpaRepository<KanbanProject, Long> projectJpa;
    @Autowired org.springframework.data.jpa.repository.JpaRepository<ProjectMember, Long> memberJpa;
    @Autowired jakarta.persistence.EntityManager em;

    private User leader;
    private User worker;
    private Company company;

    @BeforeEach
    void setUp() {
        String uid = UUID.randomUUID().toString().substring(0, 8);

        company = companyJpa.save(Company.builder().name("TestCo-" + uid).build());

        leader = userJpa.save(User.builder()
                .name("Leader").username("leader-" + uid)
                .email("leader-" + uid + "@t.com").password("hash")
                .providerType(AuthProviderType.EMAIL)
                .userType(UserType.LEADER)
                .company(company).roles(Set.of()).build());

        worker = userJpa.save(User.builder()
                .name("Worker").username("worker-" + uid)
                .email("worker-" + uid + "@t.com").password("hash")
                .providerType(AuthProviderType.EMAIL)
                .userType(UserType.WORKER)
                .company(company).roles(Set.of()).build());

        em.flush();
        em.clear();
        leader = userJpa.findById(leader.getId()).orElseThrow();
        worker = userJpa.findById(worker.getId()).orElseThrow();
        company = companyJpa.findById(company.getId()).orElseThrow();

        when(currentUserUtil.getCurrentUser()).thenReturn(leader);
    }

    // ─── createProject ────────────────────────────────────────────────

    @Test
    @DisplayName("createProject: LEADER создаёт проект и автоматически становится LEADER-участником")
    void createProject_success() {
        ProjectResponseDTO result = projectService.createProject(new ProjectCreateDTO("My Project"));

        assertThat(result.name()).isEqualTo("My Project");
        assertThat(result.id()).isNotNull();
        assertThat(result.members()).hasSize(1);
        assertThat(result.members().get(0).username()).isEqualTo(leader.getUsername());
    }

    @Test
    @DisplayName("createProject: IllegalStateException если не LEADER")
    void createProject_notLeader() {
        leader.setUserType(UserType.WORKER);

        assertThatThrownBy(() -> projectService.createProject(new ProjectCreateDTO("X")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("LEADER");
    }

    @Test
    @DisplayName("createProject: IllegalStateException если нет компании")
    void createProject_noCompany() {
        leader.setCompany(null);

        assertThatThrownBy(() -> projectService.createProject(new ProjectCreateDTO("X")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no company");
    }

    // ─── listMyProjects ───────────────────────────────────────────────

    @Test
    @DisplayName("listMyProjects: возвращает только проекты компании где пользователь участник")
    void listMyProjects_success() {
        projectService.createProject(new ProjectCreateDTO("Project 1"));
        projectService.createProject(new ProjectCreateDTO("Project 2"));

        List<ProjectResponseDTO> result = projectService.listMyProjects();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(ProjectResponseDTO::name)
                .containsExactlyInAnyOrder("Project 1", "Project 2");
    }

    @Test
    @DisplayName("listMyProjects: пусто для пользователя без проектов")
    void listMyProjects_empty() {
        when(currentUserUtil.getCurrentUser()).thenReturn(worker);

        List<ProjectResponseDTO> result = projectService.listMyProjects();

        assertThat(result).isEmpty();
    }

    // ─── getProject ───────────────────────────────────────────────────

    @Test
    @DisplayName("getProject: возвращает DTO проекта с участниками")
    void getProject_success() {
        ProjectResponseDTO created = projectService.createProject(new ProjectCreateDTO("Project A"));

        ProjectResponseDTO result = projectService.getProject(created.id());

        assertThat(result.name()).isEqualTo("Project A");
        assertThat(result.companyId()).isEqualTo(company.getId());
    }

    @Test
    @DisplayName("getProject: EntityNotFoundException при несуществующем проекте")
    void getProject_notFound() {
        assertThatThrownBy(() -> projectService.getProject(99999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Project not found");
    }

    // ─── deleteProject ────────────────────────────────────────────────

    @Test
    @DisplayName("deleteProject: LEADER удаляет свой проект")
    void deleteProject_success() {
        ProjectResponseDTO created = projectService.createProject(new ProjectCreateDTO("To Delete"));

        projectService.deleteProject(created.id());

        assertThatThrownBy(() -> projectService.getProject(created.id()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("deleteProject: EntityNotFoundException при несуществующем проекте")
    void deleteProject_notFound() {
        assertThatThrownBy(() -> projectService.deleteProject(99999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    // ─── addWorkerToProject ───────────────────────────────────────────

    @Test
    @DisplayName("addWorkerToProject: LEADER добавляет WORKER в проект")
    void addWorkerToProject_success() {
        ProjectResponseDTO project = projectService.createProject(new ProjectCreateDTO("Team"));

        ProjectResponseDTO result = projectService.addWorkerToProject(project.id(), worker.getId());

        assertThat(result.members()).hasSize(2);
        assertThat(result.members()).extracting(m -> m.username())
                .contains(worker.getUsername());
    }

    @Test
    @DisplayName("addWorkerToProject: IllegalStateException если не LEADER")
    void addWorkerToProject_notLeader() {
        ProjectResponseDTO project = projectService.createProject(new ProjectCreateDTO("Team"));
        leader.setUserType(UserType.WORKER);

        assertThatThrownBy(() -> projectService.addWorkerToProject(project.id(), worker.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("addWorkerToProject: IllegalStateException если worker уже в проекте")
    void addWorkerToProject_alreadyMember() {
        ProjectResponseDTO project = projectService.createProject(new ProjectCreateDTO("Team"));
        projectService.addWorkerToProject(project.id(), worker.getId());

        assertThatThrownBy(() -> projectService.addWorkerToProject(project.id(), worker.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already in this project");
    }

    @Test
    @DisplayName("addWorkerToProject: IllegalStateException если добавляемый не WORKER")
    void addWorkerToProject_targetNotWorker() {
        worker.setUserType(UserType.LEADER);
        ProjectResponseDTO project = projectService.createProject(new ProjectCreateDTO("Team"));

        assertThatThrownBy(() -> projectService.addWorkerToProject(project.id(), worker.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("WORKER");
    }
}
