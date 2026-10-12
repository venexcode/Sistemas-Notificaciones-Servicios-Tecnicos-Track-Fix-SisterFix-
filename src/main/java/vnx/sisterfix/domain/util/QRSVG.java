package vnx.sisterfix.domain.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Map;

/** Genera un codigo QR como SVG en linea: nitido al imprimir y sin archivos intermedios. */
public final class QRSVG {

    private QRSVG() {
    }

    public static String generar(String contenido) {
        try {
            Map<EncodeHintType, Object> hints = Map.of(
                    EncodeHintType.MARGIN, 0,
                    EncodeHintType.CHARACTER_SET, "UTF-8",
                    EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);

            BitMatrix matriz = new QRCodeWriter().encode(contenido, BarcodeFormat.QR_CODE, 0, 0, hints);
            int ancho = matriz.getWidth();
            int alto = matriz.getHeight();

            StringBuilder trazo = new StringBuilder();
            for (int y = 0; y < alto; y++) {
                for (int x = 0; x < ancho; x++) {
                    if (matriz.get(x, y)) {
                        trazo.append('M').append(x).append(' ').append(y).append("h1v1h-1z");
                    }
                }
            }

            return "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 " + ancho + " " + alto
                    + "\" shape-rendering=\"crispEdges\" role=\"img\" aria-label=\"Código QR de seguimiento\">"
                    + "<path d=\"" + trazo + "\" fill=\"currentColor\"/></svg>";
        } catch (WriterException e) {
            throw new IllegalStateException("No se pudo generar el código QR.", e);
        }
    }
}