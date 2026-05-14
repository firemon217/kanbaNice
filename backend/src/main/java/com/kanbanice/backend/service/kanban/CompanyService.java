package com.kanbanice.backend.service.kanban;

import com.kanbanice.backend.dto.kanban.CompanyCreateDTO;
import com.kanbanice.backend.dto.kanban.CompanyResponseDTO;
import com.kanbanice.backend.dto.kanban.WorkerRequestDTO;
import com.kanbanice.backend.dto.UserResponseDTO;
import com.kanbanice.backend.Repository.UserRepository;
import com.kanbanice.backend.entity.Company;
import com.kanbanice.backend.entity.User;
import com.kanbanice.backend.entity.type.UserType;
import com.kanbanice.backend.repository.kanban.KanbanProjectRepository;
import com.kanbanice.backend.repository.kanban.KanbaniceUserCompanyRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.stream.Collectors;
import java.util.List;

import jakarta.persistence.EntityNotFoundException;


@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CurrentUserUtil currentUserUtil;
    private final KanbaniceUserCompanyRepository companyRepository;
    private final KanbanProjectRepository projectRepository;
    private final UserRepository userRepository;

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
        return new CompanyResponseDTO(company.getId(), company.getName(), List.of());
    }

    @Transactional(readOnly = true)
    public CompanyResponseDTO getMyCompany() {
        User currentUser = currentUserUtil.getCurrentUser();
        if (currentUser.getCompany() == null) {
            throw new EntityNotFoundException("Company not found");
        }
        
        Company company = currentUser.getCompany();
        
        List<User> companyUsers = userRepository.findAllByCompany_Id(company.getId());
        
        List<UserResponseDTO> userDTOs = companyUsers.stream()
            .map(user -> new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getUserType()
            ))
            .collect(Collectors.toList());
        
        return new CompanyResponseDTO(
            company.getId(), 
            company.getName(),
            userDTOs
        );
    }

    @Transactional
    public void addWorkerToMyCompany(WorkerRequestDTO dto) {
        User currentUser = currentUserUtil.getCurrentUser();
        if (currentUser.getUserType() != UserType.LEADER) {
            throw new IllegalStateException("Only LEADER can add workers to company");
        }

        Company company = currentUser.getCompany();
        if (company == null) {
            throw new IllegalStateException("Leader has no company");
        }

        User worker = userRepository.findByEmail(dto.getEmail())
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
    public void deleteWorkerToMyCompany(Long id) {
        User currentUser = currentUserUtil.getCurrentUser();
        if (currentUser.getUserType() != UserType.LEADER) {
            throw new IllegalStateException("Only LEADER can delete workers to company");
        }

        Company company = currentUser.getCompany();
        if (company == null) {
            throw new IllegalStateException("Leader has no company");
        }

        User worker = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (worker.getUserType() != UserType.WORKER) {
            throw new IllegalStateException("Only WORKER can be deleted to company");
        }

        if (worker.getCompany() == null) {
            throw new IllegalStateException("Worker not in your company");
        }
        else
        {
            if (!worker.getCompany().getId().equals(company.getId())) {
                throw new IllegalStateException("Worker already belongs to another company");
            }
        }

        worker.setCompany(null);
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

        List<User> companyUsers = userRepository.findAllByCompany_Id(company.getId());
        
        List<UserResponseDTO> userDTOs = companyUsers.stream()
            .map(user -> new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getUserType()
            ))
            .collect(Collectors.toList());
        
        return new CompanyResponseDTO(
            company.getId(), 
            company.getName(),
            userDTOs
        );
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

