package techlab.proyectoFinal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import techlab.proyectoFinal.dto.ProductDTO;
import techlab.proyectoFinal.dto.ProductsDTO;
import techlab.proyectoFinal.entity.Product;
import techlab.proyectoFinal.repository.ProductRepository;
import techlab.proyectoFinal.repository.ProductRepositoryJPA;

import java.text.Normalizer;
import java.util.*;

@Service
public class ProductService {
    private final ProductRepositoryJPA repositoryJpa;
    // private final ProductRepository repository;

    @Autowired
    public ProductService(ProductRepository repository, ProductRepositoryJPA repositoryJpa) {
        // this.repository = repository;
        this.repositoryJpa = repositoryJpa;
    }

    public ProductsDTO listProducts() {
        //return this.repository.listProducts();
        ProductsDTO response = new ProductsDTO();

        List<Product> products = repositoryJpa.findAll();
        if (products.isEmpty()) {
            response.setStatus(false);
            response.setMessage("No hay productos.");
            response.setProducts(Collections.emptyList());
        } else {
            response.setStatus(true);
            response.setMessage("Productos encontrados.");
            response.setProducts(products);
        }
        return response;
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
        existingProduct.setCategory(nuevosDatos.getCategory());
        existingProduct.setImage(nuevosDatos.getImage());

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

    public List<ProductDTO> searchByName(String search) {
        String normalizedSearch = normalizer(search);

        // Trae productos cuyo nombre contenga el texto (case-insensitive)
        List<Product> foundProducts = repositoryJpa.findByNameContainingIgnoreCase(normalizedSearch);

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

    public ProductDTO updatePrice(Long id, Double newPrice) {
        ProductDTO responseDto = new ProductDTO();

        if (newPrice == null || newPrice <= 0) {
            responseDto.setMessage("El precio debe ser mayor que cero.");
            responseDto.setStatus(false);
            return responseDto;
        }

        Optional<Product> optionalProduct = repositoryJpa.findById(id);
        if (optionalProduct.isEmpty()) {
            responseDto.setMessage("No se pudó actualizar producto.");
            responseDto.setStatus(false);
            return responseDto;
        }

        Product existingProduct = optionalProduct.get();
        existingProduct.setPrice(newPrice);

        Product updatedProduct = repositoryJpa.save(existingProduct);

        responseDto.setMessage("Precio actualizado correctamente");
        responseDto.setId(updatedProduct.getId());
        responseDto.setName(updatedProduct.getName());
        responseDto.setStatus(true);

        return responseDto;
    }

    public ProductDTO updateStock(Long id, Integer newStock) {
        ProductDTO dto = new ProductDTO();

        // Validación
        if (newStock == null || newStock < 0) {
            dto.setMessage("El stock no puede ser negativo.");
            dto.setStatus(false);
            return dto;
        }

        Optional<Product> optionalProduct = repositoryJpa.findById(id);

        if (optionalProduct.isEmpty()) {
            dto.setMessage("No se pudo actualizar el stock. Producto no encontrado.");
            dto.setStatus(false);
            return dto;
        }

        Product product = optionalProduct.get();
        product.setStock(newStock);

        repositoryJpa.save(product);

        dto.setStatus(true);
        dto.setMessage("Stock actualizado correctamente.");
        dto.setId(product.getId());
        dto.setName(product.getName());

        return dto;
    }

    public ProductsDTO searchByPrice(String order) {
        ProductsDTO dto = new ProductsDTO();
        List<Product> products = repositoryJpa.findAll();

        if (products.isEmpty()) {
            dto.setStatus(false);
            dto.setMessage("No hay productos.");
            dto.setProducts(Collections.emptyList());
            return dto;
        }

        if ("desc".equalsIgnoreCase(order)) {
            products.sort(Comparator.comparing(Product::getPrice).reversed());
        } else {
            products.sort(Comparator.comparing(Product::getPrice));

            dto.setStatus(true);
            dto.setMessage("Productos ordenados por precio " + (order != null ? order.toUpperCase() : "ASC") + ".");
            dto.setProducts(products);
        }
        return dto;
    }

    public ProductsDTO searchByStock(String order) {
        ProductsDTO dto = new ProductsDTO();
        List<Product> products = repositoryJpa.findAll();

        if (products.isEmpty()) {
            dto.setStatus(false);
            dto.setMessage("No hay productos.");
            dto.setProducts(Collections.emptyList());
            return dto;
        }

        // Ordena según el stock
        if ("desc".equalsIgnoreCase(order)) {
            products.sort(Comparator.comparing(Product::getStock).reversed());
        } else {
            products.sort(Comparator.comparing(Product::getStock));
        }

        dto.setStatus(true);
        dto.setMessage("Productos ordenados por stock " + order.toUpperCase() + ".");
        dto.setProducts(products);

        return dto;
    }
}
