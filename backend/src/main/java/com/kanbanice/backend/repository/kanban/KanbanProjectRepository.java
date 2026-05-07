package com.kanbanice.backend.repository.kanban;

import com.kanbanice.backend.entity.kanban.KanbanProject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KanbanProjectRepository extends JpaRepository<KanbanProject, Long> {
}

