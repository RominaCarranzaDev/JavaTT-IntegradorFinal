package techlab.proyectoFinal.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class PedidoReqDTO {
    private String cliente;
    private List<OrdenRespDTO> ordenes;
}
