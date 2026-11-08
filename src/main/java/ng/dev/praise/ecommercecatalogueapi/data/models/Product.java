package ng.dev.praise.ecommercecatalogueapi.data.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.dev.praise.ecommercecatalogueapi.data.domainobjects.Money;
import ng.dev.praise.ecommercecatalogueapi.data.enums.Category;
import ng.dev.praise.ecommercecatalogueapi.data.enums.ProductStatus;
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)
    private String name;


    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Money price;

    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus availability = ProductStatus.IN_STOCK;
}
