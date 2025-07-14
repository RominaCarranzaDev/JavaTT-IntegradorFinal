package techlab.proyectoFinal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import techlab.proyectoFinal.entity.Pedido;

@Repository
public interface OrdenRepositoryJPA extends JpaRepository<Pedido, Long> {}

