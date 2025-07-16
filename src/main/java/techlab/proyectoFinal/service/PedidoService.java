package techlab.proyectoFinal.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import techlab.proyectoFinal.dto.*;
import techlab.proyectoFinal.entity.Orden;
import techlab.proyectoFinal.entity.Pedido;
import techlab.proyectoFinal.entity.Product;
import techlab.proyectoFinal.repository.PedidoRepositoryJPA;
import techlab.proyectoFinal.repository.ProductRepositoryJPA;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PedidoService {
    private final PedidoRepositoryJPA pedidoRepository;
    private final ProductRepositoryJPA productRepository;

    @Autowired
    public PedidoService(PedidoRepositoryJPA pedidoRepository, ProductRepositoryJPA productRepository) {
        this.pedidoRepository = pedidoRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public PedidoDetailDTO createPedido(PedidoReqDTO nuevoPedido) {
        PedidoDetailDTO response = new PedidoDetailDTO();
        List<OrdenDetailDTO> ordenesDTO = new ArrayList<>();

        // Crear el pedido con los datos base
        Pedido pedido = new Pedido();
        pedido.setCliente(nuevoPedido.getCliente());
        pedido.setEstado(nuevoPedido.getEstado());
        pedido.setFormaPago(nuevoPedido.getFormaPago());
        pedido.setFecha(LocalDateTime.now());

        List<Orden> ordenes = new ArrayList<>();

        // Validar y procesar cada orden
        for (OrdenReqDTO ordenDTO : nuevoPedido.getOrdenes()) {
            if (ordenDTO.getCantidad() == null || ordenDTO.getCantidad() <= 0) {
                response.setStatus(false);
                response.setMessage("La cantidad debe ser mayor a 0 para el producto con ID: " + ordenDTO.getProductoId());
                return response;
            }

            Optional<Product> optionalProduct = productRepository.findById(ordenDTO.getProductoId());

            if (optionalProduct.isEmpty()) {
                response.setStatus(false);
                response.setMessage("Producto con ID " + ordenDTO.getProductoId() + " no encontrado.");
                return response;
            }

            Product producto = optionalProduct.get();

            if (producto.getStock() < ordenDTO.getCantidad()) {
                response.setStatus(false);
                response.setMessage("Stock insuficiente para el producto: " + producto.getName() + ". Stock disponible: " + producto.getStock());
                return response;
            }

            // Descontar el stock
            producto.setStock(producto.getStock() - ordenDTO.getCantidad());
            productRepository.save(producto);

            // Crear la orden y asociarla al pedido
            Orden orden = new Orden();
            orden.setProducto(producto);
            orden.setCantidad(ordenDTO.getCantidad());
            orden.setPedido(pedido);
            orden.setPrecioUn(producto.getPrice());
            // El subtotal se calcula automáticamente con @PrePersist
            orden.calcularSubtotal(); // Forzar el cálculo antes del persist
            ordenes.add(orden);

            // Crear DTO para la respuesta
            OrdenDetailDTO ordenDTOresponse = new OrdenDetailDTO();
            ordenDTOresponse.setProducto(producto.getName());
            ordenDTOresponse.setCantidad(orden.getCantidad());
            ordenDTOresponse.setPrecioUnitario(producto.getPrice());
            // El subtotal se calculará al persistir, así que lo dejamos en null o lo calculamos igual
            ordenDTOresponse.setSubtotal(producto.getPrice() * orden.getCantidad());

            ordenesDTO.add(ordenDTOresponse);
        }

        // Asociar las órdenes al pedido
        pedido.setOrdenes(ordenes);

        // Calcular el total manualmente antes de guardar
        pedido.calcularTotal();

        // Guardar el pedido (guarda también las órdenes por Cascade)
        pedidoRepository.save(pedido);

        // Armar respuesta
        response.setId(pedido.getId());
        response.setCliente(pedido.getCliente());
        response.setFecha(pedido.getFecha());
        response.setOrdenes(ordenesDTO);
        response.setTotal(pedido.getTotal()); // Calculado por @PrePersist
        response.setStatus(true);
        response.setMessage("Pedido creado correctamente.");

        return response;
    }

    public List<PedidoDetailDTO> listarPedidos() {
        List<Pedido> pedidos = pedidoRepository.findAll();

        return pedidos.stream().map(pedido -> {
            PedidoDetailDTO dto = new PedidoDetailDTO();
            dto.setId(pedido.getId());
            dto.setCliente(pedido.getCliente());
            dto.setFecha(pedido.getFecha());
            dto.setTotal(pedido.getTotal());
            dto.setStatus(true);
            dto.setMessage("Order Found.");

            List<OrdenDetailDTO> ordenes = pedido.getOrdenes().stream().map(o -> {
                OrdenDetailDTO od = new OrdenDetailDTO();
                od.setProducto(o.getProducto().getName());
                od.setCantidad(o.getCantidad());
                od.setPrecioUnitario(o.getPrecioUn());
                od.setSubtotal(o.getSubtotal());
                return od;
            }).collect(Collectors.toList());

            dto.setOrdenes(ordenes);
            return dto;
        }).collect(Collectors.toList());
    }

    public PedidoDetailDTO getByIdPedido(Long id) {
        Optional<Pedido> optionalPedido = pedidoRepository.findById(id);
        PedidoDetailDTO dto = new PedidoDetailDTO();

        if (optionalPedido.isEmpty()) {
            dto.setStatus(false);
            dto.setMessage("Order Not Found with ID: " + id);
            dto.setOrdenes(Collections.emptyList());
            dto.setTotal(0.0);
            return dto;
        }

        Pedido pedido = optionalPedido.get();
        dto.setId(pedido.getId());
        dto.setCliente(pedido.getCliente());
        dto.setFecha(pedido.getFecha());
        dto.setTotal(pedido.getTotal());
        dto.setStatus(true);
        dto.setMessage("Order Found.");

        List<OrdenDetailDTO> ordenes = pedido.getOrdenes().stream().map(o -> {
            OrdenDetailDTO od = new OrdenDetailDTO();
            od.setProducto(o.getProducto().getName());
            od.setCantidad(o.getCantidad());
            od.setPrecioUnitario(o.getPrecioUn());
            od.setSubtotal(o.getSubtotal());
            return od;
        }).collect(Collectors.toList());

        dto.setOrdenes(ordenes);
        return dto;
    }

    public PedidoDetailDTO deletePedido(Long id) {
        PedidoDetailDTO dto = new PedidoDetailDTO();

        Optional<Pedido> optional = pedidoRepository.findById(id);

        if (optional.isEmpty()) {
            dto.setStatus(false);
            dto.setMessage("Order Not Found with ID: " + id);
            return dto;
        }

        pedidoRepository.deleteById(id);
        dto.setStatus(true);
        dto.setMessage("Order with ID: " + id + " successfully removed.");
        return dto;
    }

    @Transactional
    public PedidoDetailDTO updatePedido(Long pedidoId, PedidoReqDTO data) {
        PedidoDetailDTO dto = new PedidoDetailDTO();

        Optional<Pedido> optional = pedidoRepository.findById(pedidoId);
        if (optional.isEmpty()) {
            dto.setStatus(false);
            dto.setMessage("Order Not Found.");
            return dto;
        }

        Pedido pedido = optional.get();

        if (data.getCliente() != null) pedido.setCliente(data.getCliente());
        if (data.getEstado() != null) pedido.setEstado(data.getEstado());
        if (data.getFormaPago() != null) pedido.setFormaPago(data.getFormaPago());

        Map<Long, OrdenReqDTO> ordenesEntrantes = new HashMap<>();
        List<OrdenReqDTO> nuevasOrdenes = new ArrayList<>();

        for (OrdenReqDTO ordenDto : data.getOrdenes()) {
            if (ordenDto.getIdOrden() != null) {
                ordenesEntrantes.put(ordenDto.getIdOrden(), ordenDto);
            } else {
                nuevasOrdenes.add(ordenDto);
            }
        }

        List<Orden> ordenesFinales = new ArrayList<>();

        for (Orden orden : pedido.getOrdenes()) {
            OrdenReqDTO dtoOrden = ordenesEntrantes.get(orden.getId());

            if (dtoOrden != null) {
                Product productoActual = orden.getProducto();
                int stockDisponible = productoActual.getStock() + orden.getCantidad(); // se libera el stock anterior

                // Si cambió el producto
                if (!productoActual.getId().equals(dtoOrden.getProductoId())) {
                    Optional<Product> nuevoProdOpt = productRepository.findById(dtoOrden.getProductoId());
                    if (nuevoProdOpt.isEmpty()) {
                        dto.setStatus(false);
                        dto.setMessage("Order with ID " + dtoOrden.getProductoId() + " Not Found.");
                        return dto;
                    }

                    Product nuevoProd = nuevoProdOpt.get();
                    if (nuevoProd.getStock() < dtoOrden.getCantidad()) {
                        dto.setStatus(false);
                        dto.setMessage("Insufficient stock for product: " + nuevoProd.getName());
                        return dto;
                    }

                    // Restaurar stock anterior
                    productoActual.setStock(productoActual.getStock() + orden.getCantidad());
                    productRepository.save(productoActual);

                    // Descontar nuevo producto
                    nuevoProd.setStock(nuevoProd.getStock() - dtoOrden.getCantidad());
                    productRepository.save(nuevoProd);

                    orden.setProducto(nuevoProd);
                    orden.setPrecioUn(nuevoProd.getPrice());
                } else {
                    // Mismo producto, validar stock
                    if (stockDisponible < dtoOrden.getCantidad()) {
                        dto.setStatus(false);
                        dto.setMessage("Insufficient stock for product: " + productoActual.getName());
                        return dto;
                    }

                    // Actualizar stock
                    productoActual.setStock(stockDisponible - dtoOrden.getCantidad());
                    productRepository.save(productoActual);
                }

                orden.setCantidad(dtoOrden.getCantidad());
                orden.calcularSubtotal();
                ordenesFinales.add(orden);
            }
            // Si no vino, no se agrega y se elimina
        }

        for (OrdenReqDTO nueva : nuevasOrdenes) {
            Optional<Product> optionalProduct = productRepository.findById(nueva.getProductoId());
            if (optionalProduct.isEmpty()) {
                dto.setStatus(false);
                dto.setMessage("Product with ID " + nueva.getProductoId() + " Not Found.");
                return dto;
            }

            Product producto = optionalProduct.get();

            if (producto.getStock() < nueva.getCantidad()) {
                dto.setStatus(false);
                dto.setMessage("Insufficient stock for product: " + producto.getName());
                return dto;
            }

            producto.setStock(producto.getStock() - nueva.getCantidad());
            productRepository.save(producto);

            Orden orden = new Orden();
            orden.setProducto(producto);
            orden.setPedido(pedido);
            orden.setCantidad(nueva.getCantidad());
            orden.setPrecioUn(producto.getPrice());
            orden.calcularSubtotal();
            ordenesFinales.add(orden);
        }

        pedido.setOrdenes(ordenesFinales);
        pedido.calcularTotal();
        Pedido actualizado = pedidoRepository.save(pedido);

        dto.setId(actualizado.getId());
        dto.setCliente(actualizado.getCliente());
        dto.setFecha(actualizado.getFecha());
        dto.setTotal(actualizado.getTotal());
        dto.setStatus(true);
        dto.setMessage("Order updated successfully.");

        List<OrdenDetailDTO> ordenesDto = actualizado.getOrdenes().stream().map(o -> {
            OrdenDetailDTO d = new OrdenDetailDTO();
            d.setProducto(o.getProducto().getName());
            d.setCantidad(o.getCantidad());
            d.setPrecioUnitario(o.getPrecioUn());
            d.setSubtotal(o.getSubtotal());
            return d;
        }).collect(Collectors.toList());

        dto.setOrdenes(ordenesDto);
        return dto;
    }

    public String generarTicket(Pedido pedido) {
        StringBuilder sb = new StringBuilder();
        sb.append("🧾 *** TICKET DE PEDIDO ***\n");
        sb.append("ID Pedido: ").append(pedido.getId()).append("\n");
        sb.append("Cliente: ").append(pedido.getCliente()).append("\n");
        sb.append("Fecha: ").append(pedido.getFecha()).append("\n\n");

        sb.append(String.format("%-20s %-10s %-10s %-10s\n", "Producto", "Cant.", "Precio", "Subtotal"));
        sb.append("---------------------------------------------------------\n");

        for (Orden orden : pedido.getOrdenes()) {
            String nombre = orden.getProducto().getName();
            sb.append(String.format("%-20s %-10d $%-9.2f $%-9.2f\n",
                    nombre,
                    orden.getCantidad(),
                    orden.getPrecioUn(),
                    orden.getSubtotal()));
        }

        sb.append("---------------------------------------------------------\n");
        sb.append(String.format("TOTAL: $%.2f\n", pedido.getTotal()));
        sb.append("Gracias por tu compra 💚");

        return sb.toString();
    }

}
