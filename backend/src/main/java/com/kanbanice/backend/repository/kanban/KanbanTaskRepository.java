package com.kanbanice.backend.repository.kanban;

import com.kanbanice.backend.entity.kanban.KanbanBoard;
import com.kanbanice.backend.entity.kanban.KanbanTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KanbanTaskRepository extends JpaRepository<KanbanTask, Long> {

    List<KanbanTask> findAllByBoard(KanbanBoard board);
}

