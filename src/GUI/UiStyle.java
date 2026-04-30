package GUI;

import java.awt.Color;

/**
 * Constantes visuales compartidas por la interfaz Swing de GOAT & GET.
 */
final class UiStyle {

    /** Fondo claro de las pantallas principales. */
    static final Color COLOR_FONDO = Color.WHITE;

    /** Color principal de cabeceras y acciones. */
    static final Color COLOR_CABECERA = new Color(165, 143, 122);

    /** Color oscuro para texto y botones activos. */
    static final Color COLOR_TEXTO = new Color(75, 57, 43);

    /** Texto sobre fondos oscuros. */
    static final Color COLOR_TEXTO_CLARO = Color.WHITE;

    /** Color de tarjetas de producto. */
    static final Color COLOR_TARJETA = new Color(210, 190, 165);

    private UiStyle() {
    }
}
