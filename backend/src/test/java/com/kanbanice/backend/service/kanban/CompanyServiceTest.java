package com.kanbanice.backend.service.kanban;

import com.kanbanice.backend.dto.kanban.CompanyCreateDTO;
import com.kanbanice.backend.dto.kanban.CompanyResponseDTO;
import com.kanbanice.backend.dto.kanban.CompanyUpdateDTO;
import com.kanbanice.backend.dto.kanban.WorkerRequestDTO;
import com.kanbanice.backend.entity.Company;
import com.kanbanice.backend.entity.User;
import com.kanbanice.backend.entity.type.AuthProviderType;
import com.kanbanice.backend.entity.type.UserType;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("CompanyService — интеграционные unit-тесты (H2)")
class CompanyServiceTest {

    @Autowired CompanyService companyService;
    @MockBean CurrentUserUtil currentUserUtil;
    @MockBean com.kanbanice.backend.service.EmailService emailService;
    @MockBean org.springframework.security.authentication.AuthenticationManager authenticationManager;

    @Autowired org.springframework.data.jpa.repository.JpaRepository<Company, Long> companyJpa;
    @Autowired org.springframework.data.jpa.repository.JpaRepository<User, Long> userJpa;
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
                .company(null).roles(Set.of()).build());

        em.flush();
        em.clear();
        leader = userJpa.findById(leader.getId()).orElseThrow();
        worker = userJpa.findById(worker.getId()).orElseThrow();
        company = companyJpa.findById(company.getId()).orElseThrow();

        when(currentUserUtil.getCurrentUser()).thenReturn(leader);
    }

    // ─── createCompany ────────────────────────────────────────────────

    @Test
    @DisplayName("createCompany: LEADER без компании успешно создаёт")
    void createCompany_success() {
        leader.setCompany(null);

        CompanyResponseDTO result = companyService.createCompany(new CompanyCreateDTO("NewCo"));

        assertThat(result.name()).isEqualTo("NewCo");
        assertThat(result.id()).isNotNull();
    }

    @Test
    @DisplayName("createCompany: IllegalStateException если уже есть компания")
    void createCompany_alreadyHasCompany() {
        assertThatThrownBy(() -> companyService.createCompany(new CompanyCreateDTO("X")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    @DisplayName("createCompany: IllegalStateException если не LEADER")
    void createCompany_notLeader() {
        leader.setUserType(UserType.WORKER);
        leader.setCompany(null);

        assertThatThrownBy(() -> companyService.createCompany(new CompanyCreateDTO("X")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("LEADER");
    }

    // ─── getMyCompany ─────────────────────────────────────────────────

    @Test
    @DisplayName("getMyCompany: возвращает данные компании со списком сотрудников")
    void getMyCompany_success() {
        CompanyResponseDTO result = companyService.getMyCompany();

        assertThat(result.name()).isEqualTo(company.getName());
        assertThat(result.id()).isEqualTo(company.getId());
    }

    @Test
    @DisplayName("getMyCompany: EntityNotFoundException если нет компании")
    void getMyCompany_noCompany() {
        leader.setCompany(null);

        assertThatThrownBy(() -> companyService.getMyCompany())
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Company not found");
    }

    // ─── addWorkerToMyCompany ─────────────────────────────────────────

    @Test
    @DisplayName("addWorkerToMyCompany: добавляет worker в компанию лидера")
    void addWorkerToMyCompany_success() {
        WorkerRequestDTO dto = new WorkerRequestDTO();
        dto.setEmail(worker.getEmail());

        companyService.addWorkerToMyCompany(dto);

        User updated = userJpa.findById(worker.getId()).orElseThrow();
        assertThat(updated.getCompany()).isNotNull();
        assertThat(updated.getCompany().getId()).isEqualTo(company.getId());
    }

    @Test
    @DisplayName("addWorkerToMyCompany: IllegalStateException если не LEADER")
    void addWorkerToMyCompany_notLeader() {
        leader.setUserType(UserType.WORKER);
        WorkerRequestDTO dto = new WorkerRequestDTO();
        dto.setEmail(worker.getEmail());

        assertThatThrownBy(() -> companyService.addWorkerToMyCompany(dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("LEADER");
    }

    @Test
    @DisplayName("addWorkerToMyCompany: EntityNotFoundException если worker не найден")
    void addWorkerToMyCompany_workerNotFound() {
        WorkerRequestDTO dto = new WorkerRequestDTO();
        dto.setEmail("nobody@nowhere.com");

        assertThatThrownBy(() -> companyService.addWorkerToMyCompany(dto))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("addWorkerToMyCompany: IllegalStateException если worker уже в этой компании")
    void addWorkerToMyCompany_alreadyInSameCompany() {
        worker.setCompany(company);
        WorkerRequestDTO dto = new WorkerRequestDTO();
        dto.setEmail(worker.getEmail());

        assertThatThrownBy(() -> companyService.addWorkerToMyCompany(dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already in your company");
    }

    @Test
    @DisplayName("addWorkerToMyCompany: IllegalStateException если worker в другой компании")
    void addWorkerToMyCompany_differentCompany() {
        String uid2 = UUID.randomUUID().toString().substring(0, 8);
        Company other = companyJpa.save(Company.builder().name("Other-" + uid2).build());
        worker.setCompany(other);
        WorkerRequestDTO dto = new WorkerRequestDTO();
        dto.setEmail(worker.getEmail());

        assertThatThrownBy(() -> companyService.addWorkerToMyCompany(dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("another company");
    }

    // ─── deleteWorkerToMyCompany ──────────────────────────────────────

    @Test
    @DisplayName("deleteWorkerToMyCompany: убирает worker из компании")
    void deleteWorkerToMyCompany_success() {
        worker.setCompany(company);
        userJpa.save(worker);

        companyService.deleteWorkerToMyCompany(worker.getId());

        User updated = userJpa.findById(worker.getId()).orElseThrow();
        assertThat(updated.getCompany()).isNull();
    }

    @Test
    @DisplayName("deleteWorkerToMyCompany: EntityNotFoundException при несуществующем worker")
    void deleteWorkerToMyCompany_notFound() {
        assertThatThrownBy(() -> companyService.deleteWorkerToMyCompany(99999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    // ─── updateMyCompany ──────────────────────────────────────────────

    @Test
    @DisplayName("updateMyCompany: переименовывает компанию")
    void updateMyCompany_success() {
        CompanyResponseDTO result = companyService.updateMyCompany(new CompanyUpdateDTO("Renamed"));

        assertThat(result.name()).isEqualTo("Renamed");
    }

    @Test
    @DisplayName("updateMyCompany: IllegalStateException если не LEADER")
    void updateMyCompany_notLeader() {
        leader.setUserType(UserType.WORKER);

        assertThatThrownBy(() -> companyService.updateMyCompany(new CompanyUpdateDTO("X")))
                .isInstanceOf(IllegalStateException.class);
    }

    // ─── deleteMyCompany ──────────────────────────────────────────────

    @Test
    @DisplayName("deleteMyCompany: удаляет компанию")
    void deleteMyCompany_success() {
        Long companyId = company.getId();
        companyService.deleteMyCompany();

        assertThat(companyJpa.findById(companyId)).isEmpty();
    }

    @Test
    @DisplayName("deleteMyCompany: IllegalStateException если не LEADER")
    void deleteMyCompany_notLeader() {
        leader.setUserType(UserType.WORKER);

        assertThatThrownBy(() -> companyService.deleteMyCompany())
                .isInstanceOf(IllegalStateException.class);
    }
}
