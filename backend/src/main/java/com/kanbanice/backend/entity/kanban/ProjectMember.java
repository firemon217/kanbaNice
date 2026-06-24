package com.kanbanice.backend.entity.kanban;

import com.kanbanice.backend.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "project_member",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_project_member", columnNames = {"project_id", "user_id"})
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private KanbanProject project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectMemberRole role;
}

