package vnx.sisterfix.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import vnx.sisterfix.domain.enums.EstadoOrden;
import vnx.sisterfix.domain.model.Cliente;
import vnx.sisterfix.domain.model.OrdenTrabajo;
import vnx.sisterfix.repository.IClienteRepository;
import vnx.sisterfix.repository.IOrdenTrabajoRepository;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final IClienteRepository clienteRepository;
    private final IOrdenTrabajoRepository ordenTrabajoRepository;

    @Override
    public void run(String... args) {
        if (clienteRepository.count() == 0) {
            Cliente cliente = new Cliente("Juan Pérez", "1122334455", "juan@example.com");
            clienteRepository.save(cliente);

            OrdenTrabajo orden = new OrdenTrabajo();
            orden.setCodigoSeguimiento("TRK-TEST1");
            orden.setDispositivo("Notebook ASUS");
            orden.setProblemaReportado("No enciende la pantalla");
            orden.setCliente(cliente);
            orden.cambiarEstado(EstadoOrden.RECIBIDO, "Ingreso del equipo al taller.");

            ordenTrabajoRepository.save(orden);

            System.out.println("----------------------------------------------------------");
            System.out.println("✅ DATOS DE PRUEBA CARGADOS CORRECTAMENTE");
            System.out.println("Código de seguimiento para probar en la web: TRK-TEST1");
            System.out.println("----------------------------------------------------------");
        }
    }
}