package techlab.proyectoFinal.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PedidoResoDTO {
    private Long id;
    private String cliente;
    private Double total;
    private String mensaje;
    private Boolean status;
}
