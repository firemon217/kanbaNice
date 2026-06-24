package com.kanbanice.backend.service.kanban;

import com.kanbanice.backend.dto.kanban.BoardCreateDTO;
import com.kanbanice.backend.dto.kanban.BoardResponseDTO;
import com.kanbanice.backend.dto.kanban.BoardUpdateDTO;
import com.kanbanice.backend.entity.Company;
import com.kanbanice.backend.entity.User;
import com.kanbanice.backend.entity.kanban.*;
import com.kanbanice.backend.entity.type.UserType;
import com.kanbanice.backend.repository.kanban.KanbanBoardRepository;
import com.kanbanice.backend.repository.kanban.KanbanProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final CurrentUserUtil currentUserUtil;
    private final KanbanBoardRepository boardRepository;
    private final KanbanProjectRepository projectRepository;

    @Transactional(readOnly = true)
    public List<BoardResponseDTO> listBoards(Long projectId) {
        User currentUser = currentUserUtil.getCurrentUser();

        KanbanProject project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        assertSameCompany(currentUser, project.getCompany());
        assertMember(currentUser, project);

        return boardRepository.findAllByProject(project).stream()
                .map(b -> new BoardResponseDTO(
                        b.getId(),
                        b.getName(),
                        b.getProject().getId(),
                        b.getCreatedAt()
                ))
                .toList();
    }

    @Transactional
    public BoardResponseDTO createBoard(Long projectId, BoardCreateDTO dto) {
        User currentUser = currentUserUtil.getCurrentUser();

        KanbanProject project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        assertSameCompany(currentUser, project.getCompany());

        KanbanBoard board = KanbanBoard.builder()
                .name(dto.name())
                .project(project)
                .build();

        KanbanBoard saved = boardRepository.save(board);
        return new BoardResponseDTO(
                saved.getId(),
                saved.getName(),
                saved.getProject().getId(),
                saved.getCreatedAt()
        );
    }

    @Transactional
    public BoardResponseDTO updateBoard(Long boardId, BoardUpdateDTO dto) {
        User currentUser = currentUserUtil.getCurrentUser();

        KanbanBoard board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found"));

        KanbanProject project = board.getProject();
        assertSameCompany(currentUser, project.getCompany());

        board.setName(dto.name());
        KanbanBoard saved = boardRepository.save(board);
        return new BoardResponseDTO(
                saved.getId(),
                saved.getName(),
                saved.getProject().getId(),
                saved.getCreatedAt()
        );
    }

    @Transactional
    public void deleteBoard(Long boardId) {
        User currentUser = currentUserUtil.getCurrentUser();

        KanbanBoard board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found"));

        KanbanProject project = board.getProject();
        assertSameCompany(currentUser, project.getCompany());

        boardRepository.delete(board);
    }

    private void assertSameCompany(User currentUser, Company projectCompany) {
        if (projectCompany == null || currentUser.getCompany() == null || !projectCompany.getId().equals(currentUser.getCompany().getId())) {
            throw new IllegalStateException("Forbidden: different company");
        }
    }

    private void assertMember(User currentUser, KanbanProject project) {
        if (project.getMembers() == null || project.getMembers().stream()
                .noneMatch(m -> m.getUser() != null && m.getUser().getId().equals(currentUser.getId()))) {
            throw new IllegalStateException("Forbidden: not a project member");
        }
    }
}

