package techlab.proyectoFinal.service;

import techlab.proyectoFinal.dto.ProductDTO;
import techlab.proyectoFinal.entity.Product;
import techlab.proyectoFinal.repository.ProductRepository;

import java.util.List;

public class ProductService {
    private ProductRepository repositoryJpa;

    public ProductService( ProductRepository repositoryJpa) {
        this.repositoryJpa = repositoryJpa;
    }

    public List<Product> listProducts() {
        return this.repositoryJpa.findAll();
    }



    public ProductDTO addProduct(Product product) {

        Product savedProduct = this.repositoryJpa.save(product);

        ProductDTO responseDTO = new ProductDTO();
        responseDTO.setMessage("Producto creado exitosamente");
        responseDTO.setId(savedProduct.getId());
        responseDTO.setName(savedProduct.getName());

        return responseDTO;
    }


}
