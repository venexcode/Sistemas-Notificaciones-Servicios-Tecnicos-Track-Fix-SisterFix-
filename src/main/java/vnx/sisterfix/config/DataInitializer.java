package vnx.sisterfix.config;

import vnx.sisterfix.domain.enums.EstadoOrden;
import vnx.sisterfix.domain.model.Cliente;
import vnx.sisterfix.domain.model.OrdenTrabajo;
import vnx.sisterfix.repository.IClienteRepository;
import vnx.sisterfix.repository.IOrdenTrabajoRepository;
import vnx.sisterfix.domain.util.GeneradorCodigoSeguimiento;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(IClienteRepository clienteRepository, IOrdenTrabajoRepository ordenRepository) {
        return args -> {
            // 1. Crear un cliente de prueba
            Cliente cliente = new Cliente("Carlos Pérez", "+5491112345678", "carlos.perez@example.com");
            clienteRepository.save(cliente);

            // 2. Crear una orden de prueba
            OrdenTrabajo orden = new OrdenTrabajo();
            orden.setCliente(cliente);
            orden.setDispositivo("Notebook Lenovo ThinkPad T14");
            orden.setProblemaReportado("No enciende y calienta al conectar el cargador.");
            orden.setCodigoSeguimiento("TRK-TEST1");
            orden.setEstadoOrden(EstadoOrden.RECIBIDO);
            orden.cambiarEstado(EstadoOrden.RECIBIDO, "Ingreso del equipo al taller.");

            ordenRepository.save(orden);

            System.out.println("----------------------------------------------------------");
            System.out.println("✅ DATOS DE PRUEBA CARGADOS CORRECTAMENTE");
            System.out.println("Código de seguimiento para probar en la web: TRK-TEST1");
            System.out.println("----------------------------------------------------------");
        };
    }
}
