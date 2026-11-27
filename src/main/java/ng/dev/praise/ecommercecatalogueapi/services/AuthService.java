package ng.dev.praise.ecommercecatalogueapi.services;

import ng.dev.praise.ecommercecatalogueapi.dtos.request.LoginRequest;
import ng.dev.praise.ecommercecatalogueapi.dtos.request.RegisterRequest;
import ng.dev.praise.ecommercecatalogueapi.dtos.responses.AuthResponse;
import ng.dev.praise.ecommercecatalogueapi.exceptions.AuthException;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    AuthResponse register(RegisterRequest registerRequest) throws AuthException;
    AuthResponse registerAdmin(RegisterRequest request) throws AuthException;
    AuthResponse loginManually(LoginRequest request) throws AuthException;

}
