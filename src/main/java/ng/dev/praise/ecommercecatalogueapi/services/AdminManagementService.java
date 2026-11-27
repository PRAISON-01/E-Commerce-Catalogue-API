package ng.dev.praise.ecommercecatalogueapi.services;

import ng.dev.praise.ecommercecatalogueapi.data.enums.AdminAction;
import ng.dev.praise.ecommercecatalogueapi.data.enums.Status;
import ng.dev.praise.ecommercecatalogueapi.data.models.User;
import ng.dev.praise.ecommercecatalogueapi.data.repositories.UserRepository;
import ng.dev.praise.ecommercecatalogueapi.dtos.request.ProcessAdminStatusRequest;
import ng.dev.praise.ecommercecatalogueapi.dtos.responses.ProcessAdminStatusResponse;
import ng.dev.praise.ecommercecatalogueapi.exceptions.AdminProcessingException;
import ng.dev.praise.ecommercecatalogueapi.exceptions.AuthException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Service
public class AdminManagementService {
    private UserRepository userRepository;

    public AdminManagementService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ProcessAdminStatusResponse processAdminStatus(ProcessAdminStatusRequest request) throws AuthException {

        User admin = userRepository.findById(request.getAdminId())
                .orElseThrow(() -> new AuthException("Admin account not found with ID: " + request.getAdminId()));

        Status previousStatus = admin.getStatus();

        if(!admin.getRole().name().equalsIgnoreCase("ADMIN")) throw new AdminProcessingException("Error : User is not an ADMIN");



        switch(request.getAdminAction()) {
            case AdminAction.APPROVED:
                if (admin.getStatus() != Status.PENDING) {
                    throw new AdminProcessingException("Error :Only PENDING admins can be approved");
                }
                admin.setStatus(Status.ACTIVE);
                break;

            case REJECT:
                if (admin.getStatus() != Status.PENDING) {
                    throw new AdminProcessingException("Error :Can only reject an admin that is currently PENDING");
                }
                admin.setStatus(Status.IN_ACTIVE);
                break;

            case REVOKE:
                if (admin.getStatus() != Status.ACTIVE) {
                    throw new AdminProcessingException("Error :Can only revoke privileges from an ACTIVE admin");
                }
                admin.setStatus(Status.IN_ACTIVE);
                break;

        }

        User updatedAdmin = userRepository.save(admin);

        return ProcessAdminStatusResponse.builder()
                .adminId(updatedAdmin.getId())
                .email(updatedAdmin.getEmail())
                .previousStatus(previousStatus)
                .newStatus(updatedAdmin.getStatus())
                .actionTaken(request.getAdminAction())
                .processedAt(LocalDateTime.now())
                .build();
    }
}
