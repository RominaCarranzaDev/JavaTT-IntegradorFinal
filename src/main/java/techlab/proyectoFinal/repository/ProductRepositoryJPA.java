package techlab.proyectoFinal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import techlab.proyectoFinal.entity.Product;

import java.util.List;

@Repository
public interface ProductRepositoryJPA extends JpaRepository<Product, Long> {

    List<Product> findByNameContainingIgnoreCase(String search);
}
