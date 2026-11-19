package ng.dev.praise.ecommercecatalogueapi.services;

import ng.dev.praise.ecommercecatalogueapi.data.enums.AdminAction;
import ng.dev.praise.ecommercecatalogueapi.data.enums.Role;
import ng.dev.praise.ecommercecatalogueapi.data.enums.Status;
import ng.dev.praise.ecommercecatalogueapi.data.models.User;
import ng.dev.praise.ecommercecatalogueapi.data.repositories.UserRepository;
import ng.dev.praise.ecommercecatalogueapi.dtos.request.LoginRequest;
import ng.dev.praise.ecommercecatalogueapi.dtos.request.ProcessAdminStatusRequest;
import ng.dev.praise.ecommercecatalogueapi.dtos.request.RegisterRequest;
import ng.dev.praise.ecommercecatalogueapi.dtos.responses.ProcessAdminStatusResponse;
import ng.dev.praise.ecommercecatalogueapi.dtos.responses.AuthResponse;
import ng.dev.praise.ecommercecatalogueapi.exceptions.AdminProcessingException;
import ng.dev.praise.ecommercecatalogueapi.exceptions.AuthException;
import ng.dev.praise.ecommercecatalogueapi.exceptions.InvalidPasswordException;
import ng.dev.praise.ecommercecatalogueapi.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static reactor.core.publisher.Mono.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;


    @InjectMocks
    private AdminManagementService adminManagementService;


    @Test
    public void testThat_registerCustomer_duplicateEmail_throwException() {
        String duplicateEmail = "agoremiaken@gmail.com";

        RegisterRequest request = RegisterRequest.builder()
                .firstName("Agoremi")
                .lastName("Aken")
                .email(duplicateEmail)
                .phoneNumber("08012345678")
                .password("password1234")
                .build();

        Mockito.when(userRepository.existsByEmail(duplicateEmail)).thenReturn(true);

        AuthException exception = assertThrows(AuthException.class, () -> {
            authService.register(request);
        });

        assertEquals("Email address is already exist!", exception.getMessage());

        Mockito.verify(userRepository, Mockito.never()).save(any());
    }
    
    @Test
    public void testThat_registerCustomer_validDetails_validateResponse() throws AuthException {
        RegisterRequest request = RegisterRequest.builder()
                .firstName("Agoremi")
                .lastName("Aken")
                .email("agoremiaken@gmail.com")
                .phoneNumber("08012345678")
                .password("password1234")
                .build();


        Mockito.when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(passwordEncoder.encode(request.getPassword())).thenReturn("hashed_password");

        AuthResponse response = authService.register(request);

        assertEquals("User registration complete", response.getMessage());
        assertEquals("agoremiaken@gmail.com", response.getEmail());
        assertEquals(Role.CUSTOMER, response.getRole());
        assertEquals(Status.ACTIVE, response.getStatus());

        Mockito.verify(userRepository, Mockito.times(1)).save(any(User.class));


    }

    @Test
    public void testThat_registerAmin_adminStatus_isPending() throws AuthException {
        RegisterRequest request = RegisterRequest.builder()
                .firstName("Agoremi")
                .lastName("Aken")
                .email("agoremiaken@gmail.com")
                .phoneNumber("08012345678")
                .password("password1234")
                .build();

        Mockito.when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Mockito.when(passwordEncoder.encode(request.getPassword())).thenReturn("hashed_password");

        AuthResponse response = authService.registerAdmin(request);

        assertEquals("ADMIN registration PENDING", response.getMessage());
        assertEquals("agoremiaken@gmail.com", response.getEmail());
        assertEquals(Role.ADMIN, response.getRole());
        assertEquals(Status.PENDING, response.getStatus());


        Mockito.verify(userRepository, Mockito.times(1)).save(any(User.class));
    }

    @Test
    public void testThat_userRegisters_asAdmin_alreadyExistingAdminApprovesAccess() throws AuthException {
        RegisterRequest request = RegisterRequest.builder()
                .firstName("Agoremi")
                .lastName("Aken")
                .email("agoremiaken@gmail.com")
                .phoneNumber("08012345678")
                .password("password1234")
                .build();

        User mockedUser = User.builder()
                .id(1L)
                .firstName("Agoremi")
                .lastName("Aken")
                .email("agoremiaken@gmail.com")
                .phoneNumber("08012345678")
                .password("hashed_password")
                .role(Role.ADMIN)
                .status(Status.PENDING)
                .build();

        Mockito.when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(passwordEncoder.encode(request.getPassword())).thenReturn("hashed_password");

        Mockito.when(userRepository.findUserByEmail("agoremiaken@gmail.com")).thenReturn(mockedUser);
        Mockito.when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(mockedUser));

        AuthResponse response = authService.registerAdmin(request);

        assertEquals("ADMIN registration PENDING", response.getMessage());
        assertEquals("agoremiaken@gmail.com", response.getEmail());
        assertEquals(Role.ADMIN, response.getRole());
        assertEquals(Status.PENDING, response.getStatus());

        ProcessAdminStatusRequest processAdminStatusRequest = new ProcessAdminStatusRequest();
        processAdminStatusRequest.setAdminAction(AdminAction.APPROVED);
        processAdminStatusRequest.setAdminId(1L);

        ProcessAdminStatusResponse processAdminStatusResponse = adminManagementService.processAdminStatus(processAdminStatusRequest);

        User approvedUser = userRepository.findUserByEmail("agoremiaken@gmail.com");

        assertEquals(approvedUser.getStatus(), processAdminStatusResponse.getNewStatus());
        assertEquals("ACTIVE", processAdminStatusResponse.getNewStatus().name());

        Mockito.verify(userRepository, Mockito.times(2)).save(any(User.class));
    }

 @Test
    public void testThat_userIsRegistered_asAdmin_reject_userStatusIsInactive() throws AuthException {
        RegisterRequest request = RegisterRequest.builder()
                .firstName("Agoremi")
                .lastName("Aken")
                .email("agoremiaken@gmail.com")
                .phoneNumber("08012345678")
                .password("password1234")
                .build();

        User mockedUser = User.builder()
                .id(1L)
                .firstName("Agoremi")
                .lastName("Aken")
                .email("agoremiaken@gmail.com")
                .phoneNumber("08012345678")
                .password("hashed_password")
                .role(Role.ADMIN)
                .status(Status.PENDING)
                .build();

        Mockito.when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(passwordEncoder.encode(request.getPassword())).thenReturn("hashed_password");

        Mockito.when(userRepository.findUserByEmail("agoremiaken@gmail.com")).thenReturn(mockedUser);
        Mockito.when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(mockedUser));

        AuthResponse response = authService.registerAdmin(request);

        assertEquals("ADMIN registration PENDING", response.getMessage());
        assertEquals("agoremiaken@gmail.com", response.getEmail());
        assertEquals(Role.ADMIN, response.getRole());
        assertEquals(Status.PENDING, response.getStatus());

        ProcessAdminStatusRequest processAdminStatusRequest = new ProcessAdminStatusRequest();
        processAdminStatusRequest.setAdminAction(AdminAction.REJECT);
        processAdminStatusRequest.setAdminId(1L);

        ProcessAdminStatusResponse processAdminStatusResponse = adminManagementService.processAdminStatus(processAdminStatusRequest);

        User approvedUser = userRepository.findUserByEmail("agoremiaken@gmail.com");

        assertEquals(approvedUser.getStatus(), processAdminStatusResponse.getNewStatus());
        assertEquals("IN_ACTIVE", processAdminStatusResponse.getNewStatus().name());

        Mockito.verify(userRepository, Mockito.times(2)).save(any(User.class));
    }

    @Test
    public void testThat_userIsRegistered_asAdmin_revoke_userStatusIsInActive() throws AuthException {
        RegisterRequest request = RegisterRequest.builder()
                .firstName("Agoremi")
                .lastName("Aken")
                .email("agoremiaken@gmail.com")
                .phoneNumber("08012345678")
                .password("password1234")
                .build();

        User mockedUser = User.builder()
                .id(1L)
                .firstName("Agoremi")
                .lastName("Aken")
                .email("agoremiaken@gmail.com")
                .phoneNumber("08012345678")
                .password("hashed_password")
                .role(Role.ADMIN)
                .status(Status.ACTIVE)
                .build();

        Mockito.when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(passwordEncoder.encode(request.getPassword())).thenReturn("hashed_password");

        Mockito.when(userRepository.findUserByEmail("agoremiaken@gmail.com")).thenReturn(mockedUser);
        Mockito.when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(mockedUser));

        AuthResponse response = authService.registerAdmin(request);

        assertEquals("ADMIN registration PENDING", response.getMessage());
        assertEquals("agoremiaken@gmail.com", response.getEmail());
        assertEquals(Role.ADMIN, response.getRole());

        ProcessAdminStatusRequest processAdminStatusRequest = new ProcessAdminStatusRequest();
        processAdminStatusRequest.setAdminAction(AdminAction.REVOKE);
        processAdminStatusRequest.setAdminId(1L);

        ProcessAdminStatusResponse processAdminStatusResponse = adminManagementService.processAdminStatus(processAdminStatusRequest);

        User approvedUser = userRepository.findUserByEmail("agoremiaken@gmail.com");

        assertEquals(approvedUser.getStatus(), processAdminStatusResponse.getNewStatus());
        assertEquals("IN_ACTIVE", processAdminStatusResponse.getNewStatus().name());

        Mockito.verify(userRepository, Mockito.times(2)).save(any(User.class));
    }


 @Test
    public void testThat_userTriesTogetProcessAsAdmin_throwException() throws AuthException {
        RegisterRequest request = RegisterRequest.builder()
                .firstName("Agoremi")
                .lastName("Aken")
                .email("agoremiaken@gmail.com")
                .phoneNumber("08012345678")
                .password("password1234")
                .build();

        User mockedUser = User.builder()
                .id(1L)
                .firstName("Agoremi")
                .lastName("Aken")
                .email("agoremiaken@gmail.com")
                .phoneNumber("08012345678")
                .password("hashed_password")
                .role(Role.CUSTOMER)
                .status(Status.PENDING)
                .build();

        Mockito.when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(passwordEncoder.encode(request.getPassword())).thenReturn("hashed_password");

        Mockito.when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(mockedUser));

        AuthResponse response = authService.register(request);

        assertEquals("User registration complete", response.getMessage());
        assertEquals("agoremiaken@gmail.com", response.getEmail());
        assertEquals(Role.CUSTOMER, response.getRole());

        ProcessAdminStatusRequest processAdminStatusRequest = new ProcessAdminStatusRequest();
        processAdminStatusRequest.setAdminAction(AdminAction.APPROVED);
        processAdminStatusRequest.setAdminId(1L);


        AdminProcessingException exception = assertThrows(
                AdminProcessingException.class,
                ()-> adminManagementService.processAdminStatus(processAdminStatusRequest)
        );

        assertEquals("Error : User is not an ADMIN", exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(1)).save(any(User.class));
    }


@Test
    public void testThat_userisNotPending_approve_throwException() throws AuthException {
        RegisterRequest request = RegisterRequest.builder()
                .firstName("Agoremi")
                .lastName("Aken")
                .email("agoremiaken@gmail.com")
                .phoneNumber("08012345678")
                .password("password1234")
                .build();

        User mockedUser = User.builder()
                .id(1L)
                .firstName("Agoremi")
                .lastName("Aken")
                .email("agoremiaken@gmail.com")
                .phoneNumber("08012345678")
                .password("hashed_password")
                .role(Role.ADMIN)
                .status(Status.ACTIVE)
                .build();

        Mockito.when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(passwordEncoder.encode(request.getPassword())).thenReturn("hashed_password");

        Mockito.when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(mockedUser));

        AuthResponse response = authService.registerAdmin(request);

        assertEquals("ADMIN registration PENDING", response.getMessage());
        assertEquals("agoremiaken@gmail.com", response.getEmail());
        assertEquals(Role.ADMIN, response.getRole());

        ProcessAdminStatusRequest processAdminStatusRequest = new ProcessAdminStatusRequest();
        processAdminStatusRequest.setAdminAction(AdminAction.APPROVED);
        processAdminStatusRequest.setAdminId(1L);


        AdminProcessingException exception = assertThrows(
                AdminProcessingException.class,
                ()-> adminManagementService.processAdminStatus(processAdminStatusRequest)
        );

        assertEquals("Error :Only PENDING admins can be approved", exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(1)).save(any(User.class));
    }

    @Test
    public void testThat_userisNotPending_reject_throwException() throws AuthException {
        RegisterRequest request = RegisterRequest.builder()
                .firstName("Agoremi")
                .lastName("Aken")
                .email("agoremiaken@gmail.com")
                .phoneNumber("08012345678")
                .password("password1234")
                .build();

        User mockedUser = User.builder()
                .id(1L)
                .firstName("Agoremi")
                .lastName("Aken")
                .email("agoremiaken@gmail.com")
                .phoneNumber("08012345678")
                .password("hashed_password")
                .role(Role.ADMIN)
                .status(Status.ACTIVE)
                .build();

        Mockito.when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(passwordEncoder.encode(request.getPassword())).thenReturn("hashed_password");

        Mockito.when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(mockedUser));

        AuthResponse response = authService.registerAdmin(request);

        assertEquals("ADMIN registration PENDING", response.getMessage());
        assertEquals("agoremiaken@gmail.com", response.getEmail());
        assertEquals(Role.ADMIN, response.getRole());

        ProcessAdminStatusRequest processAdminStatusRequest = new ProcessAdminStatusRequest();
        processAdminStatusRequest.setAdminAction(AdminAction.REJECT);
        processAdminStatusRequest.setAdminId(1L);


        AdminProcessingException exception = assertThrows(
                AdminProcessingException.class,
                ()-> adminManagementService.processAdminStatus(processAdminStatusRequest)
        );

        assertEquals("Error :Can only reject an admin that is currently PENDING", exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(1)).save(any(User.class));
    }

    @Test
    public void testThat_userisNotPending_revoke_throwException() throws AuthException {
        RegisterRequest request = RegisterRequest.builder()
                .firstName("Agoremi")
                .lastName("Aken")
                .email("agoremiaken@gmail.com")
                .phoneNumber("08012345678")
                .password("password1234")
                .build();

        User mockedUser = User.builder()
                .id(1L)
                .firstName("Agoremi")
                .lastName("Aken")
                .email("agoremiaken@gmail.com")
                .phoneNumber("08012345678")
                .password("hashed_password")
                .role(Role.ADMIN)
                .status(Status.PENDING)
                .build();

        Mockito.when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(passwordEncoder.encode(request.getPassword())).thenReturn("hashed_password");

        Mockito.when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(mockedUser));

        AuthResponse response = authService.registerAdmin(request);

        assertEquals("ADMIN registration PENDING", response.getMessage());
        assertEquals("agoremiaken@gmail.com", response.getEmail());
        assertEquals(Role.ADMIN, response.getRole());

        ProcessAdminStatusRequest processAdminStatusRequest = new ProcessAdminStatusRequest();
        processAdminStatusRequest.setAdminAction(AdminAction.REVOKE);
        processAdminStatusRequest.setAdminId(1L);


        AdminProcessingException exception = assertThrows(
                AdminProcessingException.class,
                ()-> adminManagementService.processAdminStatus(processAdminStatusRequest)
        );

        assertEquals("Error :Can only revoke privileges from an ACTIVE admin", exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(1)).save(any(User.class));
    }

    @Test
    void loginManually_ShouldSucceed_WhenCredentialsAreValidAndActive() throws AuthException {
        LoginRequest request = new LoginRequest("praise@dev.com", "password123");

        User activeUser = User.builder()
                .email("praise@dev.com")
                .password("hashed_string")
                .status(Status.ACTIVE)
                .role(Role.ADMIN)
                .build();

        Mockito.when(userRepository.findUserByEmail("praise@dev.com")).thenReturn(activeUser);
        Mockito.when(jwtService.generateToken(any(User.class))).thenReturn("fake_jwt_token_12345");

        Mockito.when(passwordEncoder.matches("password123", "hashed_string")).thenReturn(true);

        AuthResponse response = authService.loginManually(request);

        assertEquals("Login successful! Welcome back.", response.getMessage());
    }

    @Test
    void loginManually_ShouldThrowException_WhenAdminStatusIsPending() {
        LoginRequest request = new LoginRequest("applicant@dev.com", "password123");
        User pendingAdmin = User.builder()
                .email("applicant@dev.com")
                .password("hashed_string")
                .status(Status.PENDING)
                .role(Role.ADMIN)
                .build();

        Mockito.when(userRepository.findUserByEmail("applicant@dev.com")).thenReturn(pendingAdmin);
        Mockito.when(passwordEncoder.matches("password123", "hashed_string")).thenReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            authService.loginManually(request);
        });
        assertEquals("Your admin registration is currently PENDING approval!", exception.getMessage());
    }

    @Test
    public void testThat_userIsNotActive_revoke_throwException() throws AuthException {
        RegisterRequest request = RegisterRequest.builder()
                .firstName("Agoremi")
                .lastName("Aken")
                .email("agoremiaken@gmail.com")
                .phoneNumber("08012345678")
                .password("password1234")
                .build();

        User mockedUser = User.builder()
                .id(1L)
                .firstName("Agoremi")
                .lastName("Aken")
                .email("agoremiaken@gmail.com")
                .phoneNumber("08012345678")
                .password("hashed_password")
                .role(Role.ADMIN)
                .status(Status.PENDING)
                .build();

        Mockito.when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(passwordEncoder.encode(request.getPassword())).thenReturn("hashed_password");
        Mockito.when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(mockedUser));

        AuthResponse response = authService.registerAdmin(request);

        assertEquals("ADMIN registration PENDING", response.getMessage());
        assertEquals("agoremiaken@gmail.com", response.getEmail());
        assertEquals(Role.ADMIN, response.getRole());

        ProcessAdminStatusRequest processAdminStatusRequest = new ProcessAdminStatusRequest();
        processAdminStatusRequest.setAdminAction(AdminAction.REVOKE);
        processAdminStatusRequest.setAdminId(1L);

        AdminProcessingException exception = assertThrows(
                AdminProcessingException.class,
                () -> adminManagementService.processAdminStatus(processAdminStatusRequest)
        );

        assertEquals("Error :Can only revoke privileges from an ACTIVE admin", exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(1)).save(any(User.class));
    }


    @Test
    void loginManually_userisNotActive_approve_wrongPassword_throwException() throws AuthException {
        LoginRequest request = new LoginRequest("praise@dev.com", "password123");

        User activeUser = User.builder()
                .email("praise@dev.com")
                .password("hashed_string")
                .status(Status.ACTIVE)
                .role(Role.ADMIN)
                .build();

        Mockito.when(userRepository.findUserByEmail("praise@dev.com")).thenReturn(activeUser);

        InvalidPasswordException exception = assertThrows(
                InvalidPasswordException.class,
                () -> authService.loginManually(request));

        assertEquals("Invalid email or password!", exception.getMessage());
    }


    @Test
    void loginManually_emailNotFound_throwException() throws AuthException {
        LoginRequest request = new LoginRequest("praise@dev.com", "password123");


        AuthException exception = assertThrows(
                AuthException.class,
                () -> authService.loginManually(request));

        assertEquals("Invalid email or password!", exception.getMessage());
    }

}
