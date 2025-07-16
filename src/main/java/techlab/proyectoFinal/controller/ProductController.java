package techlab.proyectoFinal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import techlab.proyectoFinal.dto.ProductDTO;
import techlab.proyectoFinal.dto.ProductUpdateDTO;
import techlab.proyectoFinal.dto.ProductsDTO;
import techlab.proyectoFinal.entity.Product;
import techlab.proyectoFinal.service.ProductService;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    private final ProductService service;

    @Autowired
    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping("/list")
    public ResponseEntity<ProductsDTO> getAllProducts(){
        ProductsDTO dto = this.service.listProducts();
        return ResponseEntity.status(dto.getStatus() ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(dto);
    }

    @PostMapping("/new")
    public ResponseEntity<ProductDTO> createProduct(@RequestBody Product product){
        ProductDTO dto = this.service.addProduct(product);
        return ResponseEntity.status(dto.getStatus() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST).body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> editProduct(@PathVariable Long id, @RequestBody Product newData){
        ProductDTO updatedProduct = service.updateProduct(id, newData);
        return  ResponseEntity.status(updatedProduct.getStatus() ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long id) {
        ProductDTO deletedProduct = service.deleteProduct(id);
        if (deletedProduct.getStatus()) {
            return ResponseEntity.ok(deletedProduct);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(deletedProduct);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getById(@PathVariable Long id){
        ProductDTO foundProduct = this.service.searchById(id);
        return ResponseEntity.status(foundProduct.getStatus() ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(foundProduct);
    }

    @GetMapping("/search")
    public ResponseEntity<ProductsDTO> searchByName(@RequestParam String name) {
        ProductsDTO result = service.searchByName(name);
        return ResponseEntity.status(result.getStatus() ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(result);
    }

    @GetMapping("/order")
    public ResponseEntity<ProductsDTO> order(@RequestParam String type, @RequestParam(required = false, defaultValue = "asc") String order) {
        ProductsDTO result = service.order(type, order);
        return ResponseEntity.status(result.getStatus() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(result);
    }

    @PutMapping("/{id}/edit")
    public ResponseEntity<ProductDTO> edit(@PathVariable Long id,@RequestBody ProductUpdateDTO updateDTO) {
        ProductDTO dto = service.editProduct(id, updateDTO.getNewPrice(), updateDTO.getNewStock());
        return ResponseEntity.status(dto.getStatus() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(dto);
    }

}
