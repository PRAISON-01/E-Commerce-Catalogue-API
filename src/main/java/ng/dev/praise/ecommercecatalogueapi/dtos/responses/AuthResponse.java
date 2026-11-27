package ng.dev.praise.ecommercecatalogueapi.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ng.dev.praise.ecommercecatalogueapi.data.enums.Role;
import ng.dev.praise.ecommercecatalogueapi.data.enums.Status;

@Data
@Builder
@AllArgsConstructor
public class AuthResponse {
    private String message;
    private long id;
    private String email;
    private Role role;
    private Status status;
    private String token;

}
