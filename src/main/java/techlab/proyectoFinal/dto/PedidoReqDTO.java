package techlab.proyectoFinal.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PedidoReqDTO {
    private String cliente;
    private List<OrdenInputDTO> ordenes;
}
