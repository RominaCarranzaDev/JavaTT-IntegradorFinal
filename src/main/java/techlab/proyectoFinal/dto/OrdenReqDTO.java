package techlab.proyectoFinal.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrdenReqDTO {
    private Long idOrden;
    private Long productoId;
    private Integer cantidad;
}
