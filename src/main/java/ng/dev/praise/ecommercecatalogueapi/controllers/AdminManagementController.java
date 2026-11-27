package ng.dev.praise.ecommercecatalogueapi.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ng.dev.praise.ecommercecatalogueapi.dtos.request.ProcessAdminStatusRequest;
import ng.dev.praise.ecommercecatalogueapi.dtos.responses.ProcessAdminStatusResponse;
import ng.dev.praise.ecommercecatalogueapi.exceptions.AdminProcessingException;
import ng.dev.praise.ecommercecatalogueapi.exceptions.AuthException;
import ng.dev.praise.ecommercecatalogueapi.services.AdminManagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/super-admin")
@RequiredArgsConstructor
public class AdminManagementController {
    private final AdminManagementService adminManagementService;

    @PatchMapping("/process-status")
//    @PreAuthorize("hasRole('SEEDED_ADMIN')")
    @PreAuthorize("hasAuthority('ROLE_SEEDED_ADMIN')")
    public ResponseEntity<ProcessAdminStatusResponse> processAdminStatus(@Valid @RequestBody ProcessAdminStatusRequest request) throws AdminProcessingException, AuthException {

        ProcessAdminStatusResponse response = adminManagementService.processAdminStatus(request);
        return ResponseEntity.ok(response);
    }

}
