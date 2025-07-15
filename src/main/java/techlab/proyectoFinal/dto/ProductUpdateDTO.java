package techlab.proyectoFinal.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductUpdateDTO {
    @Min(value = 1, message = "El ID debe ser mayor a 0")
    private Long id;

    @PositiveOrZero(message = "El precio debe ser mayor o igual a 0")
    private Double newPrice;

    @PositiveOrZero(message = "El stock debe ser mayor o igual a 0")
    private Integer newStock;
}
