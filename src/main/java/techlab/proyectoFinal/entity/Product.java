package techlab.proyectoFinal.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.text.Normalizer;

@Setter
@Getter
@Entity
@Table(name = "producto")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double price;
    private Integer stock;
    private String description;
    private String category;
    private String image;

    @Column(name = "name_normalized")
    private String normalizedname;

    public Product(){}

    public Product(String name, double price, int stock, String description, String category, String image) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.description = description;
        this.category = category;
        this.image = image;
    }

    @PrePersist
    @PreUpdate
    private void updateNameSearching() {
        if (this.name != null) {
            this.normalizedname = normalizerName();
        }
    }

    @Override
    public String toString() {
        return "Product {" +
                "name='" + name + '\'' +
                ", category='" + category + '\'' +
                '}';
    }

    public void info(){
        System.out.printf("""
                Id: %s | Nombre: %s
                --------------------------------
                Categoria: $%s
                --------------------------------
                Precio: $ %s | Stock: %s
                --------------------------------
                Detalle: %s
                ________________________________
                %s
                
                ================================
                """, this.id, this.name,this.category, this.price, this.stock, this.description, this.image);
    }

    public String normalizerName() {
        if (this.name == null) {
            return "";
        }
        // Normalizer me permitió simplificar la búsqueda y reemplazo de letras con acentos
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").toLowerCase();
    }

}
