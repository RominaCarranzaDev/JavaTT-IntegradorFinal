package techlab.proyectoFinal.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<Orden> ordenes = new ArrayList<>();

    private LocalDateTime fecha = LocalDateTime.now();
    private String cliente;
    private Double total;
    private String estado;
    private String formaPago;

    public Pedido(){}

    public Pedido(String cliente, String estado, String formaPago){
        this.cliente = cliente;
        this.estado = estado;
        this.formaPago = formaPago;
    }

    @PrePersist
    @PreUpdate
    public void calcularTotal() {
        if (ordenes != null && !ordenes.isEmpty()) {
            this.total = ordenes.stream()
                    .mapToDouble(o -> o.getSubtotal() != null ? o.getSubtotal() : 0)
                    .sum();
        } else {
            this.total = 0.0;
        }
    }
}
