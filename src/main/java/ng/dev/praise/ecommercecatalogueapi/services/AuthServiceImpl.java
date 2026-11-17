package ng.dev.praise.ecommercecatalogueapi.services;

import ng.dev.praise.ecommercecatalogueapi.data.enums.Role;
import ng.dev.praise.ecommercecatalogueapi.data.enums.Status;
import ng.dev.praise.ecommercecatalogueapi.data.models.User;
import ng.dev.praise.ecommercecatalogueapi.data.repositories.UserRepository;
import ng.dev.praise.ecommercecatalogueapi.dtos.request.LoginRequest;
import ng.dev.praise.ecommercecatalogueapi.dtos.request.RegisterRequest;
import ng.dev.praise.ecommercecatalogueapi.dtos.responses.AuthResponse;
import ng.dev.praise.ecommercecatalogueapi.exceptions.AuthException;
import ng.dev.praise.ecommercecatalogueapi.exceptions.InvalidPasswordException;
import ng.dev.praise.ecommercecatalogueapi.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;

public class AuthServiceImpl implements AuthService{
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }
    @Transactional
    @Override
    public AuthResponse register(RegisterRequest registerRequest) throws AuthException {
       if(userRepository.existsByEmail(registerRequest.getEmail())) throw new AuthException("Email address is already exist!");
       User user = User.builder()
                .firstName(registerRequest.getFirstName())
                .secondName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .phoneNumber(registerRequest.getPhoneNumber())
                .status(Status.ACTIVE)
                .password(this.passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.CUSTOMER)
                .build();


        User savedUser = userRepository.save(user);


        return AuthResponse.builder()
                .message("User registration complete")
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .id(savedUser.getId())
                .status(savedUser.getStatus())
                .build();
    }

    @Transactional
    public AuthResponse registerAdmin(RegisterRequest request) {

        User user = User.builder()
                .firstName(request.getFirstName())
                .secondName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .status(Status.PENDING)
                .password(this.passwordEncoder.encode(request.getPassword()))
                .role(Role.ADMIN)
                .build();


        User savedUser = userRepository.save(user);

        return AuthResponse.builder()
                .message(savedUser.getRole()+" registration "+ savedUser.getStatus())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .id(savedUser.getId())
                .status(savedUser.getStatus())
                .build();
    }

    public AuthResponse loginManually(LoginRequest request) throws AuthException {
        User user = userRepository.findUserByEmail(request.getEmail());

        if(user == null) throw new AuthException("Invalid email or password!");

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Invalid email or password!");
        }

        if (user.getStatus() == Status.PENDING) {
            throw new IllegalStateException("Your admin registration is currently PENDING approval!");
        }

        if (user.getStatus() == Status.IN_ACTIVE) {
            throw new IllegalStateException("Your Admin access has been deactivated!");
        }

        String jwtStringToken = jwtService.generateToken(user);


        return AuthResponse.builder()
                .message("Login successful! Welcome back.")
                .id(user.getId())
                .token(jwtStringToken)
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

}
