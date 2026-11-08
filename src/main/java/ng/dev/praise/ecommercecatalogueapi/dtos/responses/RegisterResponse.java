package ng.dev.praise.ecommercecatalogueapi.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ng.dev.praise.ecommercecatalogueapi.data.enums.Role;

@Data
@Builder
@AllArgsConstructor
public class RegisterResponse {
    private String message;
    private long id;
    private String email;
    private Role role;

}
