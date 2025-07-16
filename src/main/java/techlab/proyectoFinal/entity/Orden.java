package techlab.proyectoFinal.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@Entity
public class Orden {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer cantidad;

    @ManyToOne
    @JoinColumn(name = "pedido")
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "producto")
    private Product producto;

    private Double precioUn;

    private Double subtotal;

    public Orden() {}

    public Orden(Integer cantidad, Pedido pedido, Product producto) {
        this.cantidad = cantidad;
        this.pedido = pedido;
        this.producto = producto;
        this.precioUn = producto.getPrice();
    }

    @PrePersist
    @PreUpdate
    public void calcularSubtotal() {
        if (precioUn != null && cantidad != null) {
            this.subtotal = precioUn * cantidad;
        }
    }
}
