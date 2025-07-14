package techlab.proyectoFinal.dto;

import lombok.Getter;
import lombok.Setter;
import techlab.proyectoFinal.entity.Product;

import java.util.List;

@Getter
@Setter
public class ProductsDTO {
    private String message;
    private Boolean status;
    private List<Product> products;
}
