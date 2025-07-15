package techlab.proyectoFinal.dto;

import lombok.Getter;
import lombok.Setter;
import techlab.proyectoFinal.entity.Product;

import java.util.List;

@Getter
@Setter
public class ProductDTO {
    private String message;
    private Boolean status;
    private String name;
    private Long id;
}

