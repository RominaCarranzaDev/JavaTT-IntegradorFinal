package techlab.proyectoFinal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import techlab.proyectoFinal.dto.ProductDTO;
import techlab.proyectoFinal.entity.Product;
import techlab.proyectoFinal.service.ProductService;

import java.util.List;

public class ProductController {
    private ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping("/list")
    public List<Product> getAllProducts(){
        return this.service.listProducts();
    }

    @PostMapping("/prod")
    public ResponseEntity<ProductDTO> createProduct(@RequestBody Product product){
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.addProduct(product));
    }
}
