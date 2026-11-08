package ng.dev.praise.ecommercecatalogueapi.data.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import ng.dev.praise.ecommercecatalogueapi.data.enums.Role;
import ng.dev.praise.ecommercecatalogueapi.data.enums.Status;
@Entity
@Table(name = "user")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class User {
    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String secondName;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Status status;


    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;


}
