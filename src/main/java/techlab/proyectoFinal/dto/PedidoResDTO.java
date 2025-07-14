package techlab.proyectoFinal.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PedidoResDTO {
    private Long id;
    private String cliente;
    private Double total;
    private String mensaje;
    private Boolean status;
}
