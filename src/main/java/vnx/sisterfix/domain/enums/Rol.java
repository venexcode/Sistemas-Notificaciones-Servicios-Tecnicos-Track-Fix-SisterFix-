package vnx.sisterfix.domain.enums;

public enum Rol {
    ADMIN("Administrador"),
    TECNICO("Técnico");

    private final String etiqueta;

    Rol(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}