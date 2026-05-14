package com.kanbanice.backend.controller.kanban;

import com.kanbanice.backend.dto.kanban.CompanyCreateDTO;
import com.kanbanice.backend.dto.kanban.CompanyUpdateDTO;
import com.kanbanice.backend.dto.kanban.CompanyResponseDTO;
import com.kanbanice.backend.dto.kanban.WorkerRequestDTO;
import com.kanbanice.backend.service.kanban.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping
    public ResponseEntity<CompanyResponseDTO> getMyCompany() {
        return ResponseEntity.ok(companyService.getMyCompany());
    }

    @PostMapping
    public ResponseEntity<CompanyResponseDTO> createCompany(@Valid @RequestBody CompanyCreateDTO dto) {
        return ResponseEntity.ok(companyService.createCompany(dto));
    }

    @PostMapping("/workers")
    public ResponseEntity<Void> addWorkerToMyCompany(@RequestBody WorkerRequestDTO dto) {
        companyService.addWorkerToMyCompany(dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/workers/{id}")
    public ResponseEntity<Void> deleteWorkerToMyCompany(@PathVariable Long id) {
        companyService.deleteWorkerToMyCompany(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<CompanyResponseDTO> updateMyCompany( @RequestBody CompanyUpdateDTO dto) {
        return ResponseEntity.ok(companyService.updateMyCompany(dto));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteMyCompany() {
        companyService.deleteMyCompany();
        return ResponseEntity.noContent().build();
    }
}

