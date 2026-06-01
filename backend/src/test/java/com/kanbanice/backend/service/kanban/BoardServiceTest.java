package com.kanbanice.backend.service.kanban;

import com.kanbanice.backend.dto.kanban.BoardCreateDTO;
import com.kanbanice.backend.dto.kanban.BoardResponseDTO;
import com.kanbanice.backend.dto.kanban.BoardUpdateDTO;
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

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("BoardService — интеграционные unit-тесты (H2)")
class BoardServiceTest {

    @Autowired BoardService boardService;
    @MockBean CurrentUserUtil currentUserUtil;
    @MockBean com.kanbanice.backend.service.EmailService emailService;
    @MockBean org.springframework.security.authentication.AuthenticationManager authenticationManager;

    @Autowired org.springframework.data.jpa.repository.JpaRepository<Company, Long> companyJpa;
    @Autowired org.springframework.data.jpa.repository.JpaRepository<User, Long> userJpa;
    @Autowired org.springframework.data.jpa.repository.JpaRepository<KanbanProject, Long> projectJpa;
    @Autowired org.springframework.data.jpa.repository.JpaRepository<ProjectMember, Long> memberJpa;
    @Autowired jakarta.persistence.EntityManager em;

    private User user;
    private KanbanProject project;

    @BeforeEach
    void setUp() {
        String uid = UUID.randomUUID().toString().substring(0, 8);

        Company company = companyJpa.save(Company.builder().name("TestCo-" + uid).build());

        user = userJpa.save(User.builder()
                .name("Bob").username("bob-" + uid)
                .email("bob-" + uid + "@t.com")
                .password("hash")
                .providerType(AuthProviderType.EMAIL)
                .userType(UserType.LEADER)
                .company(company).roles(Set.of()).build());

        project = projectJpa.save(KanbanProject.builder()
                .name("Project").company(company).build());

        memberJpa.save(ProjectMember.builder()
                .project(project).user(user).role(ProjectMemberRole.LEADER).build());

        em.flush();
        em.clear();
        user = userJpa.findById(user.getId()).orElseThrow();
        project = projectJpa.findById(project.getId()).orElseThrow();

        when(currentUserUtil.getCurrentUser()).thenReturn(user);
    }

    // ─── createBoard ──────────────────────────────────────────────────

    @Test
    @DisplayName("createBoard: успешно создаёт доску")
    void createBoard_success() {
        BoardResponseDTO result = boardService.createBoard(project.getId(),
                new BoardCreateDTO("Sprint 1"));

        assertThat(result.name()).isEqualTo("Sprint 1");
        assertThat(result.projectId()).isEqualTo(project.getId());
        assertThat(result.id()).isNotNull();
    }

    @Test
    @DisplayName("createBoard: EntityNotFoundException при несуществующем проекте")
    void createBoard_projectNotFound() {
        assertThatThrownBy(() -> boardService.createBoard(99999L, new BoardCreateDTO("X")))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Project not found");
    }

    @Test
    @DisplayName("createBoard: IllegalStateException при другой компании")
    void createBoard_differentCompany() {
        String uid2 = UUID.randomUUID().toString().substring(0, 8);
        Company other = companyJpa.save(Company.builder().name("Other-" + uid2).build());
        user.setCompany(other);

        assertThatThrownBy(() -> boardService.createBoard(project.getId(), new BoardCreateDTO("X")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("different company");
    }

    // ─── listBoards ───────────────────────────────────────────────────

    @Test
    @DisplayName("listBoards: возвращает список досок проекта")
    void listBoards_success() {
        boardService.createBoard(project.getId(), new BoardCreateDTO("Board 1"));
        boardService.createBoard(project.getId(), new BoardCreateDTO("Board 2"));

        List<BoardResponseDTO> result = boardService.listBoards(project.getId());

        assertThat(result).hasSize(2);
        assertThat(result).extracting(BoardResponseDTO::name)
                .containsExactlyInAnyOrder("Board 1", "Board 2");
    }

    @Test
    @DisplayName("listBoards: EntityNotFoundException при несуществующем проекте")
    void listBoards_projectNotFound() {
        assertThatThrownBy(() -> boardService.listBoards(99999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("listBoards: IllegalStateException если не участник")
    void listBoards_notMember() {
        String uid2 = UUID.randomUUID().toString().substring(0, 8);
        User stranger = userJpa.save(User.builder()
                .name("Stranger").username("s-" + uid2)
                .email("s-" + uid2 + "@t.com").password("hash")
                .providerType(AuthProviderType.EMAIL)
                .userType(UserType.WORKER)
                .company(user.getCompany()).roles(Set.of()).build());
        when(currentUserUtil.getCurrentUser()).thenReturn(stranger);

        assertThatThrownBy(() -> boardService.listBoards(project.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not a project member");
    }

    // ─── updateBoard ──────────────────────────────────────────────────

    @Test
    @DisplayName("updateBoard: успешно переименовывает доску")
    void updateBoard_success() {
        BoardResponseDTO created = boardService.createBoard(project.getId(),
                new BoardCreateDTO("Old Name"));

        BoardResponseDTO result = boardService.updateBoard(created.id(),
                new BoardUpdateDTO("New Name"));

        assertThat(result.name()).isEqualTo("New Name");
    }

    @Test
    @DisplayName("updateBoard: EntityNotFoundException при несуществующей доске")
    void updateBoard_notFound() {
        assertThatThrownBy(() -> boardService.updateBoard(99999L, new BoardUpdateDTO("X")))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Board not found");
    }

    // ─── deleteBoard ──────────────────────────────────────────────────

    @Test
    @DisplayName("deleteBoard: успешно удаляет доску")
    void deleteBoard_success() {
        BoardResponseDTO board = boardService.createBoard(project.getId(),
                new BoardCreateDTO("To Delete"));

        boardService.deleteBoard(board.id());

        List<BoardResponseDTO> boards = boardService.listBoards(project.getId());
        assertThat(boards).isEmpty();
    }

    @Test
    @DisplayName("deleteBoard: EntityNotFoundException при несуществующей доске")
    void deleteBoard_notFound() {
        assertThatThrownBy(() -> boardService.deleteBoard(99999L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
