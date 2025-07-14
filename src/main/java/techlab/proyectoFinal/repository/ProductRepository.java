package techlab.proyectoFinal.repository;

import org.springframework.stereotype.Repository;
import techlab.proyectoFinal.entity.Product;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductRepository {
    private final ArrayList<Product> productos;

    public ProductRepository() {
        this.productos = new ArrayList<>();
        this.agregarProductosIniciales();
    }

    public List<Product> listProducts() {
        return this.productos;
    }

    public Product addProduct(Product producto){
        productos.add(producto);
        return producto;
    }

    public Product searchById(Long id) {
        for (Product producto : productos){
            if (producto.getId() == id){
                return producto;
            }
        }

        return null;
    }

    public ArrayList<Product> searchByName(String search) {
        String normalizedSearch = normalizerString(search);

        ArrayList<Product> result = new ArrayList<>();

        for (Product producto : productos) {
            String normalizedProductName = normalizerString(producto.getName());

            if (normalizedProductName.contains(normalizedSearch)) {
                result.add(producto);
            }
        }

        if (result.isEmpty()) {
            return  null;
        }
        return result;
    }

    public String normalizerString(String cadena) {
        if (cadena == null) {
            return "";
        }
        // Normalizer me permitió simplificar la búsqueda y reemplazo de letras con acentos
        String normalized = Normalizer.normalize(cadena, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{M}", "");
        return normalized.toLowerCase();
    }

    public Product editProduct(Long id, Product dataUpdate) {
        if (id == null || dataUpdate == null) return null;

        for (Product producto : productos) {
            if (id.equals(producto.getId())) {
                producto.setName(dataUpdate.getName());
                producto.setPrice(dataUpdate.getPrice());
                producto.setStock(dataUpdate.getStock());
                producto.setDescription(dataUpdate.getDescription());
                return producto;
            }
        }

        return null;
    }

    public boolean deleteProduct(Long id) {
        if (id == null) return false;

        return productos.removeIf(p -> id.equals(p.getId()));
    }

    private void agregarProductosIniciales() {
        Product producto1 = new Product("Café Cappuccino", 100, 1200, "", "", "");
        Product producto2 = new Product("Café Latte", 120, 1500, "", "","");
        Product producto3 = new Product("Mocha Frappuccino", 200, 1800, "", "", "");
        productos.add(producto1);
        productos.add(producto2);
        productos.add(producto3);
    }
}
