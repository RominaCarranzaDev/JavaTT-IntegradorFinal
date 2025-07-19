package techlab.proyectoFinal.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import techlab.proyectoFinal.dto.PedidoDetailDTO;
import techlab.proyectoFinal.dto.PedidoReqDTO;
import techlab.proyectoFinal.service.PedidoService;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5500")
@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @GetMapping("/list")
    public ResponseEntity<List<PedidoDetailDTO>> listarPedidos() {
        List<PedidoDetailDTO> lista = pedidoService.listarPedidos();
        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoDetailDTO> getByIdPedido(@PathVariable Long id) {
        PedidoDetailDTO dto = pedidoService.getByIdPedido(id);
        return ResponseEntity.status(dto.getStatus() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(dto);
    }

    @PostMapping("/new")
    public ResponseEntity<PedidoDetailDTO> createPedido(@Valid @RequestBody PedidoReqDTO nuevoPedido) {
        PedidoDetailDTO dto = pedidoService.createPedido(nuevoPedido);
        return ResponseEntity.status(dto.getStatus() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST).body(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PedidoDetailDTO> deletePedido(@PathVariable Long id) {
        PedidoDetailDTO dto = pedidoService.deletePedido(id);
        return ResponseEntity.status(dto.getStatus() ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(dto);
    }

    @PutMapping("/pedido/{id}")
    public ResponseEntity<PedidoDetailDTO> updatePedido( @PathVariable Long id, @RequestBody PedidoReqDTO data) {
        PedidoDetailDTO dto = pedidoService.updatePedido(id, data);
        return ResponseEntity.status(dto.getStatus() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(dto);
    }

}
