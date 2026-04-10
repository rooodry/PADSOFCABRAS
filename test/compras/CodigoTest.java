package compras;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;


public class CodigoTest {
	@Test
	void testCodigoGeneradoNoEsNulo() {
		Codigo codigo = new Codigo();
		assertNotNull(codigo.getCodigo(), "El código no puede ser nulo");
	}
	
	@Test
	void testCodigosSonUnicod() {
		Codigo c1 = new Codigo();
		Codigo c2 = new Codigo();
		assertNotEquals(c1.getCodigo(), c2.getCodigo(), "Dos códigos distintos no pueden ser iguales");
	}
}
