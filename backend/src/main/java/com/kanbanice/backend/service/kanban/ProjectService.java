package com.kanbanice.backend.service.kanban;

import com.kanbanice.backend.dto.kanban.*;
import com.kanbanice.backend.entity.Company;
import com.kanbanice.backend.entity.User;
import com.kanbanice.backend.entity.kanban.*;
import com.kanbanice.backend.repository.kanban.KanbanProjectRepository;
import com.kanbanice.backend.repository.kanban.ProjectMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final CurrentUserUtil currentUserUtil;
    private final KanbanProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;

    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> listMyProjects() {
        User currentUser = currentUserUtil.getCurrentUser();

        // Минимально: фильтруем по членству и компании в памяти.
        // Для производительности потом добавим repository methods.
        return projectRepository.findAll().stream()
                .filter(p -> p.getCompany() != null && currentUser.getCompany() != null
                        && p.getCompany().getId().equals(currentUser.getCompany().getId()))
                .filter(p -> p.getMembers() != null && p.getMembers().stream()
                        .anyMatch(m -> m.getUser() != null && m.getUser().getId().equals(currentUser.getId())))
                .map(this::toProjectResponse)
                .toList();
    }

    @Transactional
    public ProjectResponseDTO createProject(ProjectCreateDTO dto) {
        User currentUser = currentUserUtil.getCurrentUser();
        Company company = currentUser.getCompany();

        if (company == null) {
            throw new IllegalStateException("User has no company");
        }

        KanbanProject project = KanbanProject.builder()
                .name(dto.name())
                .company(company)
                .build();

        KanbanProject saved = projectRepository.save(project);

        ProjectMember leader = ProjectMember.builder()
                .project(saved)
                .user(currentUser)
                .role(ProjectMemberRole.LEADER)
                .build();

        projectMemberRepository.save(leader);
        saved.getMembers().add(leader);

        return toProjectResponse(saved);
    }

    @Transactional(readOnly = true)
    public ProjectResponseDTO getProject(Long projectId) {
        User currentUser = currentUserUtil.getCurrentUser();

        KanbanProject project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        assertSameCompany(currentUser, project.getCompany());
        assertMember(currentUser, project);

        return toProjectResponse(project);
    }

    @Transactional
    public void deleteProject(Long projectId) {
        User currentUser = currentUserUtil.getCurrentUser();

        KanbanProject project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        assertSameCompany(currentUser, project.getCompany());

        ProjectMember member = getProjectMember(currentUser, project);
        if (member.getRole() != ProjectMemberRole.LEADER) {
            throw new IllegalStateException("Only leader can delete project");
        }

        projectRepository.delete(project);
    }

    private void assertSameCompany(User currentUser, Company projectCompany) {
        if (projectCompany == null || currentUser.getCompany() == null
                || !projectCompany.getId().equals(currentUser.getCompany().getId())) {
            throw new IllegalStateException("Forbidden: different company");
        }
    }

    private void assertMember(User currentUser, KanbanProject project) {
        if (project.getMembers() == null || project.getMembers().stream()
                .noneMatch(m -> m.getUser() != null && m.getUser().getId().equals(currentUser.getId()))) {
            throw new IllegalStateException("Forbidden: not a project member");
        }
    }

    private ProjectMember getProjectMember(User currentUser, KanbanProject project) {
        if (project.getMembers() == null) {
            throw new IllegalStateException("Forbidden: not a project member");
        }
        return project.getMembers().stream()
                .filter(m -> m.getUser() != null && m.getUser().getId().equals(currentUser.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Forbidden: not a project member"));
    }

    private ProjectResponseDTO toProjectResponse(KanbanProject project) {
        List<ProjectMemberResponseDTO> members = project.getMembers().stream()
                .map(m -> new ProjectMemberResponseDTO(
                        m.getUser().getId(),
                        m.getUser().getUsername(),
                        m.getUser().getName(),
                        m.getRole()
                ))
                .sorted(Comparator.comparing(ProjectMemberResponseDTO::username, Comparator.nullsLast(String::compareTo)))
                .toList();

        return new ProjectResponseDTO(
                project.getId(),
                project.getName(),
                project.getCompany().getId(),
                project.getCreatedAt(),
                members
        );
    }
}
