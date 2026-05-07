package com.kanbanice.backend.service.kanban;

import com.kanbanice.backend.dto.kanban.CompanyCreateDTO;
import com.kanbanice.backend.dto.kanban.CompanyResponseDTO;
import com.kanbanice.backend.entity.Company;
import com.kanbanice.backend.entity.User;
import com.kanbanice.backend.entity.type.UserType;
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
    public CompanyResponseDTO updateMyCompany(String name) {
        User currentUser = currentUserUtil.getCurrentUser();
        if (currentUser.getCompany() == null) {
            throw new EntityNotFoundException("Company not found");
        }
        currentUser.getCompany().setName(name);
        userRepository.save(currentUser);
        return new CompanyResponseDTO(currentUser.getCompany().getId(), currentUser.getCompany().getName());
    }
}

