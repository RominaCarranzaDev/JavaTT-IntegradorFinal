package techlab.proyectoFinal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import techlab.proyectoFinal.dto.OrdenRespDTO;
import techlab.proyectoFinal.dto.PedidoReqDTO;
import techlab.proyectoFinal.dto.PedidoRespDTO;
import techlab.proyectoFinal.entity.Orden;
import techlab.proyectoFinal.entity.Pedido;
import techlab.proyectoFinal.entity.Product;
import techlab.proyectoFinal.repository.PedidoRepositoryJPA;
import techlab.proyectoFinal.repository.ProductRepositoryJPA;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoServicie {
    private final PedidoRepositoryJPA pedidoRepository;
    private final ProductRepositoryJPA productRepository;

    @Autowired
    public PedidoServicie(PedidoRepositoryJPA pedidoRepository, ProductRepositoryJPA productRepository) {
        this.pedidoRepository = pedidoRepository;
        this.productRepository = productRepository;
    }

    public PedidoRespDTO createPedido(PedidoReqDTO dto) {
        Pedido pedido = new Pedido();
        pedido.setCliente(dto.getCliente());
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstado("pendiente");

        List<Orden> ordenes = new ArrayList<>();
        double total = 0;

        for (OrdenRespDTO o : dto.getOrdenes()) {
            Optional<Product> optionalProduct = productRepository.findById(o.getProductoId());

            if (optionalProduct.isEmpty()) {
                return error("Producto con ID " + o.getProductoId() + " no encontrado.");
            }

            Product producto = optionalProduct.get();

            if (producto.getStock() < o.getCantidad()) {
                return error("Stock insuficiente para el producto " + producto.getName());
            }

            producto.setStock(producto.getStock() - o.getCantidad());

            Orden orden = new Orden();
            orden.setProducto(producto);
            orden.setCantidad(o.getCantidad());
            orden.setPrecioUn(producto.getPrice());
            orden.setPedido(pedido);

            ordenes.add(orden);
            total += orden.getSubtotal();
        }

        pedido.setOrdenes(ordenes);
        pedidoRepository.save(pedido);

        PedidoRespDTO response = new PedidoRespDTO();
        response.setId(pedido.getId());
        response.setCliente(pedido.getCliente());
        response.setTotal(total);
        response.setStatus(true);
        response.setMensaje("Pedido creado exitosamente.");

        return response;
    }

    private PedidoRespDTO error(String mensaje) {
        PedidoRespDTO error = new PedidoRespDTO();
        error.setStatus(false);
        error.setMensaje(mensaje);
        return error;
    }
}
