package ng.dev.praise.ecommercecatalogueapi.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.dev.praise.ecommercecatalogueapi.data.enums.AdminAction;

@Data
@AllArgsConstructor @NoArgsConstructor
public class ProcessAdminStatusRequest {
    @NotBlank(message = "Admin id cannot be null")
    private long adminId;

    @NotBlank(message = "Admin Action cannot be null")
    private AdminAction adminAction;

}
