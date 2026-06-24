package com.kanbanice.backend.repository.kanban;

import com.kanbanice.backend.entity.Company;
import com.kanbanice.backend.entity.kanban.KanbanProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KanbanProjectRepository extends JpaRepository<KanbanProject, Long> {
    List<KanbanProject> findAllByCompany(Company company);
}

