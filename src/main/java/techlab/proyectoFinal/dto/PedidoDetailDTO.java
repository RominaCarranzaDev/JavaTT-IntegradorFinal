package techlab.proyectoFinal.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class PedidoDetailDTO {
    private Long id;
    private String cliente;
    private LocalDateTime fecha;
    private Double total;
    private List<OrdenDetailDTO> ordenes;

    private Boolean status;
    private String message;
}
