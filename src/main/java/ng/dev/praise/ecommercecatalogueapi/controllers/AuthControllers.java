package ng.dev.praise.ecommercecatalogueapi.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ng.dev.praise.ecommercecatalogueapi.dtos.request.LoginRequest;
import ng.dev.praise.ecommercecatalogueapi.dtos.request.RegisterRequest;
import ng.dev.praise.ecommercecatalogueapi.dtos.responses.AuthResponse;
import ng.dev.praise.ecommercecatalogueapi.exceptions.AuthException;
import ng.dev.praise.ecommercecatalogueapi.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthControllers {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerCustomer(@Valid @RequestBody RegisterRequest request) throws AuthException {
        AuthResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/register-admin")
    public ResponseEntity<AuthResponse> registerAdmin(@Valid @RequestBody RegisterRequest request) throws AuthException {
        AuthResponse response = authService.registerAdmin(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) throws AuthException {
        AuthResponse response = authService.loginManually(request);
        return ResponseEntity.ok(response);
    }
}
