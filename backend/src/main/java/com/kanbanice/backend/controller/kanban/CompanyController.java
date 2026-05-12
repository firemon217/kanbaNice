package com.kanbanice.backend.controller.kanban;

import com.kanbanice.backend.dto.kanban.CompanyCreateDTO;
import com.kanbanice.backend.dto.kanban.CompanyResponseDTO;
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

    @GetMapping("/me")
    public ResponseEntity<CompanyResponseDTO> getMyCompany() {
        return ResponseEntity.ok(companyService.getMyCompany());
    }

    @PostMapping
    public ResponseEntity<CompanyResponseDTO> createCompany(@Valid @RequestBody CompanyCreateDTO dto) {
        return ResponseEntity.ok(companyService.createCompany(dto));
    }

    @PostMapping("/workers")
    public ResponseEntity<Void> addWorkerToMyCompany(@RequestParam String email) {
        companyService.addWorkerToMyCompany(email);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/workers")
    public ResponseEntity<Void> deleteWorkerToMyCompany(@RequestParam String email) {
        companyService.deleteWorkerToMyCompany(email);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<CompanyResponseDTO> updateMyCompany(@RequestParam String name) {
        return ResponseEntity.ok(companyService.updateMyCompany(name));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteMyCompany() {
        companyService.deleteMyCompany();
        return ResponseEntity.noContent().build();
    }
}

