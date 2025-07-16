package techlab.proyectoFinal.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrdenDetailDTO {
    private String producto;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;
}

