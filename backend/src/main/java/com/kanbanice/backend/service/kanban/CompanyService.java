package com.kanbanice.backend.service.kanban;

import com.kanbanice.backend.dto.kanban.CompanyCreateDTO;
import com.kanbanice.backend.dto.kanban.CompanyResponseDTO;
import com.kanbanice.backend.entity.Company;
import com.kanbanice.backend.entity.User;
import com.kanbanice.backend.entity.type.UserType;
import com.kanbanice.backend.repository.kanban.KanbanProjectRepository;
import com.kanbanice.backend.repository.kanban.KanbaniceUserCompanyRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;


@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CurrentUserUtil currentUserUtil;
    private final com.kanbanice.backend.Repository.UserRepository userRepository;
    private final KanbaniceUserCompanyRepository companyRepository;
    private final KanbanProjectRepository projectRepository;

    @Transactional
    public CompanyResponseDTO createCompany(CompanyCreateDTO dto) {
        User currentUser = currentUserUtil.getCurrentUser();

        if (currentUser.getCompany() != null) {
            throw new IllegalStateException("Company already exists for this user");
        }

        if (currentUser.getUserType() != UserType.LEADER) {
            throw new IllegalStateException("Only LEADER can create company");
        }

        Company company = Company.builder()
                .name(dto.name())
                .build();

        company = companyRepository.save(company);
        currentUser.setCompany(company);
        userRepository.save(currentUser);
        return new CompanyResponseDTO(company.getId(), company.getName());
    }

    @Transactional(readOnly = true)
    public CompanyResponseDTO getMyCompany() {
        User currentUser = currentUserUtil.getCurrentUser();
        if (currentUser.getCompany() == null) {
            throw new EntityNotFoundException("Company not found");
        }
        return new CompanyResponseDTO(currentUser.getCompany().getId(), currentUser.getCompany().getName());
    }

    @Transactional
    public void addWorkerToMyCompany(Long userId) {
        User currentUser = currentUserUtil.getCurrentUser();
        if (currentUser.getUserType() != UserType.LEADER) {
            throw new IllegalStateException("Only LEADER can add workers to company");
        }

        Company company = currentUser.getCompany();
        if (company == null) {
            throw new IllegalStateException("Leader has no company");
        }

        User worker = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (worker.getUserType() != UserType.WORKER) {
            throw new IllegalStateException("Only WORKER can be added to company");
        }

        if (worker.getCompany() != null) {
            if (worker.getCompany().getId().equals(company.getId())) {
                throw new IllegalStateException("Worker already in your company");
            }
            throw new IllegalStateException("Worker already belongs to another company");
        }

        worker.setCompany(company);
        userRepository.save(worker);
    }

    @Transactional
    public CompanyResponseDTO updateMyCompany(String name) {
        User currentUser = currentUserUtil.getCurrentUser();
        if (currentUser.getUserType() != UserType.LEADER) {
            throw new IllegalStateException("Only LEADER can update company");
        }
        if (currentUser.getCompany() == null) {
            throw new EntityNotFoundException("Company not found");
        }

        Company company = companyRepository.findById(currentUser.getCompany().getId())
                .orElseThrow(() -> new EntityNotFoundException("Company not found"));
        company.setName(name);
        Company savedCompany = companyRepository.save(company);

        return new CompanyResponseDTO(savedCompany.getId(), savedCompany.getName());
    }

    @Transactional
    public void deleteMyCompany() {
        User currentUser = currentUserUtil.getCurrentUser();
        if (currentUser.getUserType() != UserType.LEADER) {
            throw new IllegalStateException("Only LEADER can delete company");
        }
        if (currentUser.getCompany() == null) {
            throw new EntityNotFoundException("Company not found");
        }

        Company company = companyRepository.findById(currentUser.getCompany().getId())
                .orElseThrow(() -> new EntityNotFoundException("Company not found"));

        projectRepository.deleteAll(projectRepository.findAllByCompany(company));

        for (User user : userRepository.findAllByCompany_Id(company.getId())) {
            user.setCompany(null);
            userRepository.save(user);
        }

        companyRepository.delete(company);
    }
}

