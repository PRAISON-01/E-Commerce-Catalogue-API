package ng.dev.praise.ecommercecatalogueapi.services;

import ng.dev.praise.ecommercecatalogueapi.dtos.request.RegisterRequest;
import ng.dev.praise.ecommercecatalogueapi.dtos.responses.AuthResponse;
import ng.dev.praise.ecommercecatalogueapi.exceptions.AuthException;

public interface AuthService {
    AuthResponse register(RegisterRequest registerRequest) throws AuthException;
    AuthResponse registerAdmin(RegisterRequest request) throws AuthException;
}
