package com.kanbanice.backend.repository.kanban;

import com.kanbanice.backend.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KanbaniceUserCompanyRepository extends JpaRepository<Company, Long> {
}

