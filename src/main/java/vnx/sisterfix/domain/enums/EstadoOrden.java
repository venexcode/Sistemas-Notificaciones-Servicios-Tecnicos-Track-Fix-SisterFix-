package vnx.sisterfix.domain.enums;

public enum EstadoOrden {
    RECIBIDO("Recibido", "Tu equipo ya está en el taller, esperando su turno."),
    EN_DIAGNOSTICO("En diagnóstico", "Estamos revisando qué le pasa."),
    EN_REPARACION("En reparación", "Ya estamos trabajando en tu equipo."),
    LISTO_PARA_RETIRAR("Listo para retirar", "Tu equipo está listo. Puedes pasar a retirarlo con tu comprobante."),
    ENTREGADO("Entregado", "Retiraste tu equipo. Gracias por confiar en nosotras."),
    CANCELADO("Cancelado", "Esta orden fue cancelada. Si tienes dudas, consúltanos en el taller.");

    private final String etiqueta;
    private final String descripcion;

    EstadoOrden(String etiqueta, String descripcion) {
        this.etiqueta = etiqueta;
        this.descripcion = descripcion;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public String getDescripcion() {
        return descripcion;
    }
}