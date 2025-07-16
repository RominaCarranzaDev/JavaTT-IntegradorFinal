package techlab.proyectoFinal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
        ProductsDTO dto = new ProductsDTO();

        List<Product> products = repositoryJpa.findAll();
        if (products.isEmpty()) {
            dto.setStatus(false);
            dto.setMessage("No products found.");
            dto.setProducts(Collections.emptyList());
        } else {
            dto.setStatus(true);
            dto.setMessage("Products found.");
            dto.setProducts(products);
            dto.setQuantity(products.size());
        }
        return dto;
    }

    public ProductDTO addProduct(Product product) {
        ProductDTO dto = new ProductDTO();
        try {
            Product savedProduct = this.repositoryJpa.save(product);
            dto.setMessage("Successfully created product.");
            dto.setStatus(true);
            dto.setId(savedProduct.getId());
            dto.setName(savedProduct.getName());
            return dto;
        } catch (Exception e) {
            dto.setMessage("Error creating new product: " + e.getMessage());
            dto.setStatus(false);

        }
        return dto;
    }

    public ProductDTO updateProduct(Long id, Product newData) {
        Optional<Product> optionalProduct = repositoryJpa.findById(id);
        ProductDTO dto = new ProductDTO();

        if (optionalProduct.isEmpty()) {
            dto.setMessage("Error updating product.");
            dto.setStatus(false);
            return dto;
        }

        Product existingProduct = optionalProduct.get();

        existingProduct.setName(newData.getName());
        existingProduct.setPrice(newData.getPrice());
        existingProduct.setStock(newData.getStock());
        existingProduct.setDescription(newData.getDescription());
        existingProduct.setCategory(newData.getCategory());
        existingProduct.setImage(newData.getImage());

        Product updatedProduct = repositoryJpa.save(existingProduct);

        dto.setMessage("Product updated successfully.");
        dto.setId(updatedProduct.getId());
        dto.setName(updatedProduct.getName());
        dto.setStatus(true);

        return dto;
    }

    public ProductDTO deleteProduct(Long id) {
        Optional<Product> optionalProduct = repositoryJpa.findById(id);
        ProductDTO dto = new ProductDTO();

        if (optionalProduct.isEmpty()) {
            dto.setMessage("Error deleting product with ID: " + id);
            dto.setStatus(false);
            return dto;
        }

        repositoryJpa.deleteById(id);
        dto.setMessage("Product with ID: " + id + " successfully removed");
        dto.setStatus(true);
        dto.setId(id);
        return dto;
    }

    public ProductDTO searchById(Long id) {
        ProductDTO dto = new ProductDTO();
        Optional<Product> optionalProduct = repositoryJpa.findById(id);

        if (optionalProduct.isPresent()) {
            Product producto = optionalProduct.get();
            dto.setId(producto.getId());
            dto.setName(producto.getName());
            dto.setStatus(true);
            dto.setMessage("Product found.");
        } else {
            dto.setStatus(false);
            dto.setMessage("Product not found with id ID: " + id);
        }
        return dto;
    }

    public ProductsDTO searchByName(String search) {
        ProductsDTO dto = new ProductsDTO();
        String normalizedSearch = normalizer(search);

        if (search == null || search.trim().isEmpty()) {
            dto.setStatus(false);
            dto.setMessage("Search query error.");
            dto.setProducts(Collections.emptyList());
            dto.setQuantity(0);
            return dto;
        }

        List<Product> foundProducts = repositoryJpa.findByNameContainingIgnoreCase(normalizedSearch);
        List<Product> result = new ArrayList<>();

        for (Product producto : foundProducts) {
            String nombreNormalizado = producto.normalizerName();
            if (nombreNormalizado.contains(normalizedSearch)) {
                result.add(producto);
            }
        }
        if (result.isEmpty()) {
            dto.setStatus(false);
            dto.setMessage("Products not found.");
        } else {
            dto.setStatus(true);
            dto.setMessage("Products found.");
        }

        dto.setProducts(result);
        dto.setQuantity(result.size());

        return dto;
    }

    public String normalizer(String text) {
        if (text == null) {
            return "";
        }
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").toLowerCase();
    }

    public ProductsDTO order(String type, String order) {
        ProductsDTO dto = new ProductsDTO();
        List<Product> products;

        String sortOrder = (order != null && order.equalsIgnoreCase("desc")) ? "desc" : "asc";

        switch (type.toLowerCase()) {
            case "price":
                products = sortOrder.equals("asc")
                        ? repositoryJpa.findAll(Sort.by("price").ascending())
                        : repositoryJpa.findAll(Sort.by("price").descending());
                break;

            case "stock":
                products = sortOrder.equals("asc")
                        ? repositoryJpa.findAll(Sort.by("stock").ascending())
                        : repositoryJpa.findAll(Sort.by("stock").descending());
                break;

            default:
                dto.setStatus(false);
                dto.setMessage("Error sorting by type.");
                dto.setProducts(Collections.emptyList());
                dto.setQuantity(0);
                return dto;
        }

        dto.setStatus(true);
        dto.setMessage("Products sorted by " + type + " (" + sortOrder + ")");
        dto.setProducts(products);
        dto.setQuantity(products.size());
        return dto;
    }

    public ProductDTO editProduct(Long id, Double newPrice, Integer newStock) {
        ProductDTO dto = new ProductDTO();

        Optional<Product> optionalProduct = repositoryJpa.findById(id);
        if (optionalProduct.isEmpty()) {
            dto.setStatus(false);
            dto.setMessage("Product not found with id ID: " + id);
            return dto;
        }

        Product product = optionalProduct.get();

        boolean actualizado = false;

        // Validaciones y actualizaciones
        if (newPrice != null) {
            if (newPrice <= 0) {
                dto.setStatus(false);
                dto.setMessage("El precio debe ser mayor a cero.");
                return dto;
            }
            product.setPrice(newPrice);
            actualizado = true;
        }

        if (newStock != null) {
            if (newStock < 0) {
                dto.setStatus(false);
                dto.setMessage("El stock no puede ser negativo.");
                return dto;
            }
            product.setStock(newStock);
            actualizado = true;
        }

        if (!actualizado) {
            dto.setStatus(false);
            dto.setMessage("Error updating product.");
            return dto;
        }

        Product updated = repositoryJpa.save(product);

        dto.setId(updated.getId());
        dto.setName(updated.getName());
        dto.setStatus(true);
        dto.setMessage("Product updated successfully.");
        return dto;
    }

}
