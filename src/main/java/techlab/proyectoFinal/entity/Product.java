package techlab.proyectoFinal.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.text.Normalizer;

@Setter
@Getter
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private double price;
    private int stock;
    private String description;

    public Product(){}

    public Product(String name, double price, int stock, String description) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.description = description;
    }

    public void info(){
        System.out.printf("""
                Id: %s | Nombre: %s
                --------------------------------
                Precio: $ %s | Stock: %s
                --------------------------------
                Detalle: %s
                ================================
                """, this.id, this.name, this.price, this.stock, this.description);
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
