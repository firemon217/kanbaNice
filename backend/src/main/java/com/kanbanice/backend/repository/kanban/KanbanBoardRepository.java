package com.kanbanice.backend.repository.kanban;

import com.kanbanice.backend.entity.kanban.KanbanBoard;
import com.kanbanice.backend.entity.kanban.KanbanProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KanbanBoardRepository extends JpaRepository<KanbanBoard, Long> {

    List<KanbanBoard> findAllByProject(KanbanProject project);
}

