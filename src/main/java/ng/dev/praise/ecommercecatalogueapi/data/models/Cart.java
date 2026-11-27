package ng.dev.praise.ecommercecatalogueapi.data.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "carts")
public class Cart {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private long id;

    @Column(nullable = true, name = "guest_token")
    private String guestToken;

    @Column(name = "customer_id")
    private long customerId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private List<CartItem> items = new ArrayList<>();
}
