package com.crm.controller;

import com.crm.repository.ClienteRepository;
import com.crm.repository.ProspectoRepository;
import com.crm.repository.VentaRepository;
import com.crm.repository.CotizacionRepository;
import com.crm.repository.PedidoRepository;
import com.crm.repository.ServicioClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {
    
    private final ClienteRepository clienteRepository;
    private final ProspectoRepository prospectoRepository;
    private final VentaRepository ventaRepository;
    private final CotizacionRepository cotizacionRepository;
    private final PedidoRepository pedidoRepository;
    private final ServicioClienteRepository servicioClienteRepository;
    
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("clientesActivos", clienteRepository.count());
        stats.put("prospectos", prospectoRepository.count());
        stats.put("ventas", ventaRepository.count());
        stats.put("cotizaciones", cotizacionRepository.count());
        stats.put("pedidos", pedidoRepository.count());
        stats.put("tickets", servicioClienteRepository.count());
        
        return stats;
    }
    
    @GetMapping("/ventas-mes")
    public Map<String, Object> getVentasMes() {
        Map<String, Object> data = new HashMap<>();
        data.put("total", ventaRepository.count());
        data.put("monto", 0.0);
        return data;
    }
}
