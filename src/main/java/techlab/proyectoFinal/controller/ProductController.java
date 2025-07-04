package techlab.proyectoFinal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import techlab.proyectoFinal.dto.ProductDTO;
import techlab.proyectoFinal.entity.Product;
import techlab.proyectoFinal.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping("/list")
    public List<Product> getAllProducts(){
        return this.service.listProducts();
    }

    @PostMapping("/new_prod")
    public ResponseEntity<ProductDTO> createProduct(@RequestBody Product product){
        ProductDTO dto = this.service.addProduct(product);
        if(dto.getStatus()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getById(@PathVariable Long id){
        ProductDTO foundProduct = this.service.searchById(id);
        if(foundProduct.getStatus()) {
            return ResponseEntity.status(HttpStatus.OK).body(foundProduct);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(foundProduct);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchByName(@RequestParam String name) {
        List<ProductDTO> result = service.searchByName(name);

        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }

        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> editProduct(@PathVariable Long id, @RequestBody Product nuevaData){
        ProductDTO updatedProduct = service.updateProduct(id, nuevaData);
        if (updatedProduct.getStatus()) {
            return  ResponseEntity.status(HttpStatus.OK).body(updatedProduct);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long id) {
        ProductDTO deletedProduct = service.deleteProduct(id);

        if (deletedProduct.getStatus()) {
            return ResponseEntity.ok(deletedProduct);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(deletedProduct);
    }
}
