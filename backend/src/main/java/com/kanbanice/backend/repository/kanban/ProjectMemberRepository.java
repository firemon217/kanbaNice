package com.kanbanice.backend.repository.kanban;

import com.kanbanice.backend.entity.User;
import com.kanbanice.backend.entity.kanban.KanbanProject;
import com.kanbanice.backend.entity.kanban.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    Optional<ProjectMember> findByProjectAndUser(KanbanProject project, User user);
}

