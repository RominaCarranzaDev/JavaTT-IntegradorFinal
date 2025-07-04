package techlab.proyectoFinal.service;

import techlab.proyectoFinal.dto.ProductDTO;
import techlab.proyectoFinal.entity.Product;
import techlab.proyectoFinal.repository.ProductRepository;
import techlab.proyectoFinal.repository.ProductRepositoryJPA;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ProductService {
    private ProductRepositoryJPA repositoryJpa;
    private ProductRepository repository;

    public ProductService(ProductRepository repository, ProductRepositoryJPA repositoryJpa) {
        this.repository = repository;
        this.repositoryJpa = repositoryJpa;
    }

    public List<Product> listProducts() {
        return this.repositoryJpa.findAll();
    }

    public ProductDTO addProduct(Product product) {
        ProductDTO responseDTO = new ProductDTO();
        try {
            Product savedProduct = this.repositoryJpa.save(product);

            responseDTO.setMessage("Producto creado exitosamente");
            responseDTO.setId(savedProduct.getId());
            responseDTO.setName(savedProduct.getName());
            responseDTO.setStatus(true);
        } catch (Exception e) {
            responseDTO.setMessage("Error al crear el producto: " + e.getMessage());
            responseDTO.setStatus(false);
        }
        return responseDTO;
    }

    public ProductDTO searchById(Long id) {
        ProductDTO responseDto = new ProductDTO();

        Optional<Product> resultado = repositoryJpa.findById(id);

        if (resultado.isPresent()) {
            Product producto = resultado.get();
            responseDto.setId(producto.getId());
            responseDto.setName(producto.getName());
            responseDto.setStatus(true);
            responseDto.setMessage("Producto encontrado!");
        } else {
            responseDto.setStatus(false);
            responseDto.setMessage("No se encontró ningún producto con ID: " + id);
        }

        return responseDto;
    }

    public ProductDTO updateProduct(Long id, Product nuevosDatos) {
        Optional<Product> optionalProduct = repositoryJpa.findById(id);
        ProductDTO responseDto = new ProductDTO();

        if (optionalProduct.isEmpty()) {
            responseDto.setMessage("No se pudó actualizar producto.");
            responseDto.setStatus(false);
            return responseDto;
        }

        Product existingProduct = optionalProduct.get();

        existingProduct.setName(nuevosDatos.getName());
        existingProduct.setPrice(nuevosDatos.getPrice());
        existingProduct.setStock(nuevosDatos.getStock());
        existingProduct.setDescription(nuevosDatos.getDescription());

        Product updatedProduct = repositoryJpa.save(existingProduct);

        responseDto.setMessage("Producto actualizado correctamente");
        responseDto.setId(updatedProduct.getId());
        responseDto.setName(updatedProduct.getName());
        responseDto.setStatus(true);

        return responseDto;
    }

    public ProductDTO deleteProduct(Long id) {
        Optional<Product> optionalProduct = repositoryJpa.findById(id);
        ProductDTO responseDto = new ProductDTO();

        if (optionalProduct.isEmpty()) {
            responseDto.setMessage("No se pudo eliminar el producto con ID: " + id);
            responseDto.setStatus(false);
            return responseDto;
        }

        repositoryJpa.deleteById(id);
        responseDto.setMessage("Producto eliminado correctamente con ID: " + id);
        responseDto.setStatus(true);

        return responseDto;
    }

    public List<ProductDTO> searchByName(String search){
        if (search == null || search.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String normalizedSearch = normalizer(search);

        // Trae productos cuyo nombre contenga el texto (case-insensitive)
        List<Product> foundProducts = repositoryJpa.findByNameContainingIgnoreCase(search);

        List<ProductDTO> result = new ArrayList<>();

        for (Product producto : foundProducts) {
            String nombreNormalizado = producto.normalizerName();

            if (nombreNormalizado.contains(normalizedSearch)) {
                ProductDTO dto = new ProductDTO();
                dto.setId(producto.getId());
                dto.setName(producto.getName());
                dto.setStatus(true);
                dto.setMessage("Found ..."); // opcional
                result.add(dto);
            }
        }

        return result;
    }
    public String normalizer(String text) {
        if (text == null) {
            return "";
        }
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").toLowerCase();
    }
}
