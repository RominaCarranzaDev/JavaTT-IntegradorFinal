package techlab.proyectoFinal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import techlab.proyectoFinal.dto.ProductDTO;
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

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getById(@PathVariable Long id){
        ProductDTO foundProduct = this.service.searchById(id);
        return ResponseEntity.status(foundProduct.getStatus() ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(foundProduct);
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

    @PutMapping("/{id}/q")
    public ResponseEntity<ProductDTO> update(@PathVariable Long id, @RequestParam(required = false) Double newPrice, @RequestParam(required = false) Integer newStock){
        ProductDTO dto = new ProductDTO();
        if (newPrice == null && newStock == null) {
            dto.setStatus(false);
            dto.setMessage("Datos faltantes");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dto);
        }

        if (newPrice != null) {
            dto = service.updatePrice(id, newPrice);
            if (!dto.getStatus()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dto);
            }
        }

        if (newStock != null) {
            dto = service.updateStock(id, newStock);
            if (!dto.getStatus()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dto);
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @GetMapping("/filter")
    public ResponseEntity<ProductsDTO> filter(@RequestParam String type, @RequestParam(required = false, defaultValue = "asc") String order) {
        ProductsDTO result;

        switch (type.toLowerCase()) {
            case "price":
                result = service.searchByPrice(order);
                break;
            case "stock":
                result = service.searchByStock(order);
                break;
            default:
                result = new ProductsDTO();
                result.setStatus(false);
                result.setMessage("Filtro inválido.");
                result.setProducts(Collections.emptyList());
        }
        return ResponseEntity
                .status(result.getStatus() ? HttpStatus.OK : HttpStatus.NOT_FOUND)
                .body(result);
    }
}
