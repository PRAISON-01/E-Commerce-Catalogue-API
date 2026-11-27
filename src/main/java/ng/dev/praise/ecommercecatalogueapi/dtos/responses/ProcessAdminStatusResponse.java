package ng.dev.praise.ecommercecatalogueapi.dtos.responses;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.dev.praise.ecommercecatalogueapi.data.enums.AdminAction;
import ng.dev.praise.ecommercecatalogueapi.data.enums.Status;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor @NoArgsConstructor
public class ProcessAdminStatusResponse {
    @NotBlank(message = "Admin Id cannot be null")
    private long adminId;

    @NotBlank(message = "Email cannot be null")
    private String email;

    @NotBlank(message = "Previous Status cannot be null")
    private Status previousStatus;

    @NotBlank(message = "new Status cannot be null")
    private Status newStatus;


    @NotBlank(message = "Action taken cannot be null")
    private AdminAction actionTaken;


    @NotBlank(message = "Processed At cannot be null")
    private LocalDateTime processedAt;
}
